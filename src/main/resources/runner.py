import sys
from landslide import mainwithnoexit

try:
    mainwithnoexit.main(args)
except Exception as e:
    print e
