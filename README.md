# Bart Pi #

# Debian Build
1. `docker run -v $(pwd):/root --rm dylanowen/sbt-packager-debian sbt debian:packageBin`
2. `scp target/bart-pi_0.2_all.deb pi@raspberrypi.local:~/`
3. `ssh pi@raspberrypi.local`
4. `sudo apt install ./bart-pi_0.2_all.deb`
5. `JAVA_OPTS="-Dbart-pi.station=12th -Dbart-pi.lines.0=RED -Dbart-pi.lines.1=YELLOW -Dbart-pi.direction=s" bash -c "bart-pi"`

# Docker
`https://blog.alexellis.io/getting-started-with-docker-on-raspberry-pi/`

<aside class="notice">
This runs much slower than simply running the java application
</aside>

## Build Manually
1. `sbt docker:publishLocal`
2. `docker save dylanowen/bart-pi:latest > ~/Desktop/bart-pi.tar && scp ~/Desktop/bart-pi.tar pi@raspberrypi.local:~/`
3. `ssh pi@raspberrypi.local`
4. `docker load -i bart-pi.tar`

## Run
`docker run --device /dev/spidev0.0 --device /dev/spidev0.1 -e JAVA_OPTS="-Dbart-pi.station=12th -Dbart-pi.lines.0=RED -Dbart-pi.direction=s" dylanowen/bart-pi:latest`

# Simple Build
`sbt assembly`

## Push to Raspberry Pi
1. `scp target/scala-2.12/bart-pi-assembly-0.1.jar pi@raspberrypi.local:~/`
2. `screen -S bart` to create the session
3. `java -jar bart-pi-assembly-0.1.jar | tee -a bart.log` to start and log the output
4. `ctrl + shift + a` then `d` to detach
4. `screen -r bart` to reconnect

# Acknowledgments
https://github.com/sharetop/max7219-java
https://max7219.readthedocs.io/en/latest/install.html#gpio-pin-outs