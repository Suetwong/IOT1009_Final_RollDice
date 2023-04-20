package com.suet.rolldice

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.suet.rolldice.R.id.day_night_mode
import com.suet.rolldice.databinding.ActivityMainBinding

class MainActivity<Item : View> : AppCompatActivity(), View.OnClickListener , AdapterView.OnItemSelectedListener{
    private lateinit var binding: ActivityMainBinding
    private lateinit var die1 : Die
    private lateinit var die2 : Die

    // flags to indicate corresponding actions
    private var rollingOneDie : Boolean = true
    private var customDieFace : Boolean = true
    private var storeList: Boolean = true
    private var dayMode: Boolean = true

    // beginning setting for defining the number of die faces
    private lateinit var arrayAdapter: ArrayAdapter<Int>
    private val basicNumSides = arrayOf(4,6,8,10,12,20)
    private var currentNumSide : Int = basicNumSides[0]

    // a empty list for user to customize the die faces
    private var dieFaceList = mutableListOf<Int>()
    private lateinit var dieFaceListAsString :String

    // a shared preference to store key-value data for re-use the customized die faces
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
        binding.numSidesSpinner.onItemSelectedListener = this
        binding.addDieFaceButton.setOnClickListener(this)

        binding.definedFacesSwitch.setOnCheckedChangeListener { _, isChecked ->
            customDieFace = isChecked
            updateDisplay()
        }

        binding.storeListSwitch.setOnCheckedChangeListener { _, isChecked ->
            storeList = isChecked
            Log.i("onclick", "store list : $isChecked")
        }

        // use the toggle button to toggle the number of  die/dice
        binding.numDieTbutton.setOnCheckedChangeListener { _, isChecked ->
            rollingOneDie = isChecked
            Log.i("Toggling", "Now turned $isChecked")
            initDie()
            updateDisplay()
        }
    }

    override fun onResume() {
        super.onResume()
        if(sharedPrefs.getBoolean("storeListPref",false)) {
            binding.definedFacesSwitch.isChecked=true
            customDieFace=true
            val stringList = sharedPrefs.getString("myVals", "")
            dieFaceList = stringList?.split(" ")?.map { it.toInt() }?.toMutableList()!!
        }
        else
        {
            dieFaceList.clear()
            customDieFace = false
        }
        //binding.definedFaceTv.setText(stringList)
        updateDisplay()

    }

    override fun onPause() {
        // create an editor to store data
        val editor = sharedPrefs.edit()
        Log.i("onClick", "storeListPref$storeList")
        if (storeList) {
            editor.putBoolean("storeListPref",true)
            editor.putString("myVals", dieFaceListAsString)
        }
        // otherwise, the data is not going to be stored
        else {
            // cleared the editor and restore the preference setting
            editor.clear()
        }

        // apply the editor
        editor.apply()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)

        // initiate the day night mode button shown as an icon instead of default title
        val dayNightButton = menu.findItem(day_night_mode)
        updateIcon(dayNightButton)
        return true
    }

    private fun updateIcon(menuItem:MenuItem){
        if (dayMode) {
            menuItem.setIcon(R.drawable.ic_day) // set the flagged icon
        } else {
            menuItem.setIcon(R.drawable.ic_night) // set the normal icon
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("ItemID", item.itemId.toString())

        when(item.itemId){

            day_night_mode -> {
                dayMode = !dayMode


                if(dayMode) {
                    updateIcon(item)
                    // crushed when changing the
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    Toast.makeText(this, "Day", Toast.LENGTH_SHORT).show()
                }
                else{
                    updateIcon(item)
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    Toast.makeText(this, "Night", Toast.LENGTH_SHORT).show()
                }

            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun initDie() {
        die1 = Die(this.currentNumSide)
        if (!rollingOneDie){
            die2 = Die(this.currentNumSide)
        }

    }

    private fun updateDisplay() {


        if(dieFaceList.isNotEmpty()){
            dieFaceListAsString = dieFaceList.joinToString(" ")
            binding.definedFaceTv.text = dieFaceListAsString
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
            binding.dieOneResultIn1Layout.text = die1.currentSideUp.toString()
            //binding.dieOneResult.setText("0")
            //binding.dieTwoResult.setText("0")

            binding.oneDiceLayout.visibility =View.VISIBLE
            binding.twoDiceLayout.visibility =View.GONE
        } else {
            //binding.dieOneResultIn1Layout.setText("0")
            binding.dieOneResult.text = die1.currentSideUp.toString()
            binding.dieTwoResult.text = die2.currentSideUp.toString()
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





