# Bart Pi #

# Build
`sbt assembly`

# Push to Raspberry Pi
1. `scp target/scala-2.12/bart-pi-assembly-0.1.jar pi@192.168.0.101:~/`
2. `screen -S bart` to create the session
3. `java -jar bart-pi-assembly-0.1.jar | tee -a bart.log` to start and log the output
4. `ctrl + shift + a` then `d` to detach
4. `screen -r bart` to reconnect

# Acknowledgments
https://github.com/sharetop/max7219-java
https://max7219.readthedocs.io/en/latest/install.html#gpio-pin-outs