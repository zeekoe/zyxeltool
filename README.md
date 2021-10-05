# zyxeltool
Tool for doing command line stuff on VMG8825-T50 and perhaps other zyxel modems

Example /etc/zyxel.properties file:
```
login={"Input_Account":"admin","Input_Passwd":"*****","currLang":"en","RememberPassword":0,"SHA512_password":false}
httpenable.url=https://192.168.1.2/cgi-bin/DAL?oid=nat
httpenable.payload={"Enable":true,"Protocol":"TCP","Description":"***","Interface":"IP.Interface.7","ExternalPortStart":80,"ExternalPortEnd":80,"InternalPortStart":80,"InternalPortEnd":80,"InternalClient":"***","SetOriginatingIP":false,"OriginatingIpAddress":"","Index":5,"X_ZYXEL_AutoDetectWanStatus":false}
httpdisable.url=https://192.168.1.2/cgi-bin/DAL?oid=nat
httpdisable.payload={"Enable":false,"Protocol":"TCP","Description":"***","Interface":"IP.Interface.7","ExternalPortStart":80,"ExternalPortEnd":80,"InternalPortStart":80,"InternalPortEnd":80,"InternalClient":"***","SetOriginatingIP":false,"OriginatingIpAddress":"","Index":5,"X_ZYXEL_AutoDetectWanStatus":false}
wifidisable2.url=https://192.168.1.2/cgi-bin/DAL?oid=wlan
wifidisable2.payload={"Index":1,"wlEnable":false}
wifidisable5.url=https://192.168.1.2/cgi-bin/DAL?oid=wlan
wifidisable5.payload={"Index":5,"wlEnable":false}
wifienable2.url=https://192.168.1.2/cgi-bin/DAL?oid=wlan
wifienable2.payload={"Index":1,"wlEnable":true}
wifienable5.url=https://192.168.1.2/cgi-bin/DAL?oid=wlan
wifienable5.payload={"Index":5,"wlEnable":true}
```

You can get the login string as well as the httpdisable/httpenable strings from the developer console in for example Firefox.

Usage example: `java -jar target/zyxeltool-1.0-SNAPSHOT-jar-with-dependencies.jar wifienable5 wifienable2`

Todo: make the base URL configurable. And cleanup the code. :)