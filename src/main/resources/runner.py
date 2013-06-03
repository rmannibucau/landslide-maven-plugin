import sys
from landslide import main

try:
    main.main(args)
except Exception as e:
    print e
