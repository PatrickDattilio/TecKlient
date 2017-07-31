package com.dattilio.scoundrel

enum class Action(priority: Int) {
    NOTHING(0),
    ATTACK(100),
    KILL(101),
    RELEASE(102),
    WIELD(103),
    GET_WEAPON(104),
    RETREAT(105),
    RECOVER(106)
}