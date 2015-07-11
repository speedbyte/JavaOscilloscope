import subprocess
import os

curdir = os.getcwd()
print curdir
os.chdir("f:\workspace_python\iCurb\CANblueCon")
os.system("CanBlueCon 49 ./Examples/CanBlueCon_CAN_RX_TX_Demo_Start.txt")
os.chdir(curdir)
