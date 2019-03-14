"""Importing the necessary modules for creating Lengaburu Class"""

import strength as strn
import troops as trp

class Lengaburu:
    horses = trp.Troops(strn.HORSE, 100)
    elephants = trp.Troops(strn.ELEPHANT, 50)
    armoured_tanks = trp.Troops(strn.ARMOURED_TANKS, 10)
    sling_guns = trp.Troops(strn.SLING_GUNS, 5)
