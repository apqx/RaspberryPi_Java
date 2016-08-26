#!/bin/bash
#start mjpg-streamer to start video
sudo LD_LIBRARY_PATH=/opt/mjpg-streamer-master/mjpg-streamer-experimental/ mjpg_streamer -i "input_raspicam.so -rot 180 -fps 30 -x 680 -y 382" -o "output_http.so -w /usr/local/www"
