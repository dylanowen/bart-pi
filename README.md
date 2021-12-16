# Bart Pi #

# Debian Build
1. `docker run -v $(pwd):/root --rm dylanowen/sbt-packager-debian sbt debian:packageBin`
2. `scp target/bart-pi_0.2_all.deb pi@raspberrypi.local:~/`
3. `ssh pi@raspberrypi.local`
4. `sudo dpkg -i bart-pi_0.2_all.deb && sudo apt-get install -f`

### Uninstall
`sudo apt-get remove bart-pi`

### Update
1. `docker run -v $(pwd):/root --rm dylanowen/sbt-packager-debian sbt debian:packageBin && scp target/bart-pi_0.2_all.deb pi@raspberrypi.local:~/`
2. `sudo apt-get remove --yes bart-pi && sudo dpkg -i bart-pi_0.2_all.deb && sudo apt-get install -f`


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