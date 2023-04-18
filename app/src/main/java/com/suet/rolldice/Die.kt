package com.suet.rolldice

// a class Die with 2 parameters
// and both numSide and name are immutable
class Die(val numSide: Int ,val name: String){

    // mutable currentSideUp as changed after rolling
    public var currentSideUp : Int

    // with 2 parameter and get the current side up
    init {
        currentSideUp = (1..numSide).random()
        //roll()

    }

    //with 1 parameter - the number of side, the name begins with "d"
    constructor(numSide:Int) : this(numSide, name = "d" + numSide) {
        // as this is the second constructor,
        // the current side up will be set up in the beginning
    }
    // with 0 parameter - default die has 6 sides and named "d6"
    constructor() : this(6,"d6") {
    }

    // to get the current side up
    fun getInfo(): String {
        var info =
            "The current side up for " + this.name + " with sides: " + this.numSide + "is " + this.currentSideUp
        return info
    }

    // to get the current side up
//    fun getCurrentSideUp(): Int {
//        return this.currentSideUp
//        //println("The current side up for " + this.name + " is " + this.currentSideUp)
//    }

    // to roll the dice (change the current side up)
    fun roll() : Int {
        this.currentSideUp = (1..numSide).random()
        return this.currentSideUp
    }

    // to show the die current side after rolling
    fun rollAndTalk(){
        println("Rolling the " + this.name)
        roll()
        if (this.currentSideUp == this.numSide){
            println("Yeah! It's " + this.currentSideUp)

        }else{
            println("The new value is " + this.currentSideUp)
        }
    }


    // to roll and count the dice until get the highest number
    fun setSideUp(){
        var counter = 0
        while(this.currentSideUp != this.numSide){
            roll()
            ++counter
        }
        //println("Rolled" + counter)
        println("The current side " + this.currentSideUp)

    }
}