#!/usr/bin/python
# -*- coding: GB2312 -*-


import sys
import getopt
import urllib
import re
import locale
import os
import time


maximum = 1 
minimum = 1 
city = "beijing"
outputfile = "/tmp/beijing.txt"
busList = {}

class Bus:
    def __init__(self, num):
        self.num = num
        self.stops = ""
        self.start = ""
        self.end = ""
        self.direction = 2
        self.time = ""
    def addNextStop(self, stop):
        stop = stop.strip()
        if self.start == "":
            self.start = stop
            self.stops = stop
        else:
            self.stops = self.stops + "-" + stop
        self.end = stop

def FetchBusInfoFromNum(busnum, url):
    print "Fetching " + url + " for " + busnum
    s = ""
    for i in range(3):
        try:
            f = urllib.urlopen(url)
            s = f.read()
            f.close()
            break
        except: 
            time.sleep(30)
    cols = s.split("\"")

    '''
    for i in range(len(cols)):
        print str(i)+"\t"+cols[i]
    '''

    time = cols[13]
    price = cols[15]
    company = cols[17]
    distance = cols[21]
    update = cols[23]
    s1 = cols[27]

    s2 = s1.split("||")
    if len(s2) != 4: 
        '''One way'''
        bus = Bus(busnum)
        bus.direction = 1
        s3 = s2[0].split("|")
        for i in range(len(s3)):
            bus.addNextStop(s3[i])
        GetTime(bus, time)
        AppendBus(bus)
    else:
        '''Two way'''
        bus = Bus(busnum+"↑")
        bus.direction = 2
        s3 = s2[0].split("|")
        for i in range(len(s3)):
            bus.addNextStop(s3[i])
        GetTime(bus, time)
        AppendBus(bus)

        bus = Bus(busnum+"↓")
        bus.direction = 2
        s3 = s2[2].split("|")
        for i in range(len(s3)):
            bus.addNextStop(s3[i])
        GetTime(bus, time)
        AppendBus(bus)

def GetTime(bus, time):

    if time.find(bus.start)<0:
        bus.time = time
        return

    patterns = ("(?:.*)(\d+:\d\d(?:-|→)\d+?:\d\d)", 
                bus.start+"((?:(?:(?:\d+:\d\d(?:(?:-|→)\d+?:\d\d)*))|、)*)"
            )
    '''bus.start+"(\d+?:\d\d(?:-|→)\d+?:\d\d(?:、\d+?:\d\d(?:-|→)\d+?:\d\d)*)(?:，|,| |\|)"+bus.end+"(\d+?:\d\d(?:-|→)\d+?:\d\d(?:、\d+?:\d\d(?:-|→)\d+?:\d\d)*)"'''
    for pattern in patterns:
        o = re.search(pattern, time)
        if o:
            g = o.groups()
            if(len(g)==1):
                bus.time = g[0]
            if(len(g)==2):
                bus.time = g[0]
            break



def AppendBus(bus):
    file = open(outputfile,"a")
    file.write(bus.num+" "+bus.stops+":{"+bus.time+"}\n")
    file.close()
    busList[bus.num] = 1

    
def FetchInfo():
    for i in range(minimum, maximum+1):

        if busList.get(str(i)) == 1:
            continue

        '''beijing'''
        url = "file:///tmp/x_b5b2988f"
        '''jiaxing'''
        url = "file:///tmp/x_94d65922"
        url = "http://"+city+".8684.cn/so.php?k=pp&q="+str(i)
        print "Fetching " + url

        s = ""
        for retry in range(3):
            try:
                f = urllib.urlopen(url)
                s = f.read()
                f.close()
                break
            except: 
                time.sleep(30)

        '''print s'''
        if re.search("没有 "+str(i)+" 这条线路,",s):
            print "No such a bus!"
            continue
        m = re.search("<li>搜索结果：(.*)</li>",s)
        if m:
            bstr = m.group();
            iterator = re.findall("<a href=\"#(.*?)\">(.*?)</a>",bstr);
            print str(len(iterator)) + " Bus(es) found:"
            for (href, bus) in iterator:
                m = re.search("http://\w+.8684.cn/\w+/(\w+)/"+href+".js", s)
                if m:
                    FetchBusInfoFromNum(bus, m.group())
                else:
                    print bus + " not found!"
                    continue
        else:
            ParseStop(i, s)
            continue

def ParseStop(num, s):
    o = re.search("<div id=\"main\">", s)
    s = s[o.end():]

    o = re.search("<script", s)
    s = s[:o.start()]

    p = s.find("去程：")
    o = re.search("\(\d+站\)</span></p>", s[p:])
    if p>=0 and o:
        stops = s[len("去程：") + p:o.start()+p]
        stops = RemoveTags(stops) 
        r = re.compile(" - ")
        stops = r.sub("-", stops) 
        bus = Bus(str(num)+"↑")
        bus.stops = stops
        stoplist = stops.split("-")
        bus.start = stoplist[0]
        bus.end = stoplist[-1]

        o = re.search("<li>(.*?)</li>", s[:p])
        if o:
            info = s[o.start()+len("<li>"):o.end()-len("</li>")]
            info = RemoveTags(info)
            infos = info.split(" ")
            time = infos[1] + infos[2]
            GetTime(bus, time)
        AppendBus(bus)


    p = s.find("回程：")
    o = re.search("\(\d+站\)</span></p>", s[p:])
    if p>=0 and o:
        stops = s[len("回程：") + p:o.start()+p]
        stops = RemoveTags(stops) 
        r = re.compile(" - ")
        stops = r.sub("-", stops) 
        bus = Bus(str(num)+"↓")
        bus.stops = stops
        stoplist = stops.split("-")
        bus.start = stoplist[0]
        bus.end = stoplist[-1]

        o = re.search("<li>(.*?)</li>", s[:p])
        if o:
            info = s[o.start()+len("<li>"):o.end()-len("</li>")]
            info = RemoveTags(info)
            infos = info.split(" ")
            time = infos[1] + infos[2]
            GetTime(bus, time)
        AppendBus(bus)





def RemoveTags(s):
    r = re.compile("<(.*?)>")
    x = r.sub("", s) 
    return x




'''if __name__ == '__main__':'''
def main(argv):
    try:
        opts, args = getopt.getopt(argv, "hc:m:M:o:d", ["help", "minimum", "maximum=", "city=", "output="]) 
    except getopt.GetoptError: 
        usage()
        sys.exit(2) 
    for opt, arg in opts:
        if opt in ["h", "--help"]:
            usage()
            sys.exit()
        elif opt == '-d':
            global _debug
            _debug = 1
        elif opt in ["-c", "city"]:
            global city;
            city = arg
        elif opt in ["-m", "--minimum"]:
            global minimum;
            minimum = int(arg)
        elif opt in ["-M", "--maximum"]:
            global maximum;
            maximum = int(arg)
        elif opt in ["-o", "--output"]:
            global outputfile;
            outputfile = arg
    try:
        if minimum == 1:
            os.remove(outputfile);
    except: 
        print "Remove output file error, maybe it does not exist"

    FetchInfo()
    sys.exit(0)

def usage():
    print "crawler.py -h host -d -c city -m minimum -M maximum -o outputfile"

if __name__ == "__main__":
    main(sys.argv[1:])
