Eric Ngor
Ezreal Clone
Requires core.jar from Processing

Contains the Floater class created by Sean Fottrell and Mindy Holmes

CURRENT FEATURES:
Moving, Attacking, Pseudo attack move with X
Mystic Shot with Q
Arcane Shift with E
Flash with D


RANGE FORMULA
Value = 0.36 * League Unit Value

DIRECTION FORMULA
//Direction in degrees
Value = (((targetX < myX) ? 180 : 0) + (180 / Math.PI) * (Math.atan((myY - targetY) / (myX - targetX))));