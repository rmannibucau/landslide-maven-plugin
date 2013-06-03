import sys
from landslide import mojomain

try:
    mojomain.main(args)
except Exception as e:
    print e
