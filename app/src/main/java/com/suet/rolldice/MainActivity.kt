package com.suet.rolldice

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.preference.PreferenceManager


import com.suet.rolldice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener , AdapterView.OnItemSelectedListener{
    private lateinit var binding: ActivityMainBinding
    public lateinit var die1 : Die
    public lateinit var die2 : Die
    public var rollingOneDie : Boolean = true
    private lateinit var arrayAdapter: ArrayAdapter<Int>
    public  val basicNumSides = arrayOf(4,6,8,10,12,20)
    public var currentNumSide : Int = basicNumSides[0]
    public var dieFaceList = mutableListOf<Int>()
    public lateinit var listAsString :String
    public var customDieFace : Boolean = true
    public var storeList: Boolean = true
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDie()

        // using a shared preferences to save and retrieve data in the form of key,value pair.
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        arrayAdapter = ArrayAdapter<Int>(this, android.R.layout.simple_list_item_1 )
        binding.numSidesSpinner.adapter = arrayAdapter
        for(element in basicNumSides){
            arrayAdapter.add(element)
        }

        binding.rollButton.setOnClickListener(this)
        binding.addDieSidesButton.setOnClickListener(this)
        binding.numSidesSpinner.setOnItemSelectedListener(this)
        binding.addDieFaceButton.setOnClickListener(this)
        binding.definedFacesSwitch.setOnCheckedChangeListener(){ _, isChecked ->
            customDieFace = isChecked
            updateDisplay()
        }


        binding.storeListSwitch.setOnCheckedChangeListener(){ _, isChecked ->
            storeList = isChecked
        }

        // use the toggle button to toggle how many die/dice is/are rolling
        binding.numDieTbutton.setOnCheckedChangeListener { _, isChecked ->
            rollingOneDie = isChecked
            Log.i("Toggling", "Now turned " + isChecked.toString())
            initDie()
            updateDisplay()
        }

        // sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

    }

    override fun onResume() {
        super.onResume()
        val stringList = sharedPrefs.getString("myVals", "")
        dieFaceList = stringList?.split(" ")?.map { it.toInt() }?.toMutableList()!!


        //binding.definedFaceTv.setText(stringList)
        updateDisplay()

    }

    override fun onPause() {
        // create an editor to store data
        val editor = sharedPrefs.edit()
        if (storeList) {
            editor.putString("myVals", listAsString)
        }
        // otherwise, the data is not going to be stored
        else {
            // cleared the editor and restore the perference setting
            editor.clear()
        }

        // apply the editor
        editor.apply()
        super.onPause()
    }

    private fun initDie() {
        die1 = Die(this.currentNumSide)
        if (!rollingOneDie){
            die2 = Die(this.currentNumSide)
        }

    }

    private fun updateDisplay() {
        if(dieFaceList.isNotEmpty()){
            listAsString = dieFaceList.joinToString(" ")
            binding.definedFaceTv.setText(listAsString)
        }

        if(customDieFace){
            binding.numSidesLayout.visibility =View.GONE
            binding.addSidesLayout.visibility =View.VISIBLE
        }
        else{
            binding.numSidesLayout.visibility =View.VISIBLE
            binding.addSidesLayout.visibility =View.GONE
        }

        if (rollingOneDie){
            binding.dieOneResultIn1Layout.setText(die1.currentSideUp.toString())
            //binding.dieOneResult.setText("0")
            //binding.dieTwoResult.setText("0")

            binding.oneDiceLayout.visibility =View.VISIBLE
            binding.twoDiceLayout.visibility =View.GONE


        } else {
            //binding.dieOneResultIn1Layout.setText("0")
            binding.dieOneResult.setText(die1.currentSideUp.toString())
            binding.dieTwoResult.setText(die2.currentSideUp.toString())
            binding.oneDiceLayout.visibility =View.GONE
            binding.twoDiceLayout.visibility =View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        Log.i("onClick", "A button is pressed")

        when (p0?.id) {


            R.id.add_die_sides_button ->{
                if (binding.newDieSidesEt.text.isNotBlank()) {
                    arrayAdapter.add(binding.newDieSidesEt.text.toString().toInt())
                    arrayAdapter.sort(compareBy<Int> { it })
                    arrayAdapter.notifyDataSetChanged()
                    binding.newDieSidesEt.text.clear()
                    Toast.makeText(this, "Added new option", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.add_die_face_button->{
                if (binding.definedFacesInput.text.isNotBlank()) {
                    dieFaceList.add(binding.definedFacesInput.text.toString().toInt())
                    binding.definedFacesInput.text.clear()
                    updateDisplay()


                }
            }
            R.id.roll_button-> {
                if (customDieFace) {
                    initDie()
                    die1.roll(dieFaceList)
                    if (!rollingOneDie) {
                        die2.roll(dieFaceList)
                    }
                }
                else
                {
                    initDie()
                    die1.roll()
                    if (!rollingOneDie) {
                        die2.roll()
                    }
                }
                // update display (scores) when the any button is pressed
                updateDisplay()
            }

            else -> Log.e("onClick", "Something went wrong")
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.i("selected", p0?.getItemAtPosition(p2).toString())
        currentNumSide = p0?.getItemAtPosition(p2).toString().toInt()
        initDie()
        updateDisplay()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        //TODO("Not yet implemented")
    }
}





