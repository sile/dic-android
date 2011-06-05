#! /bin/bash

ROOT=$HOME/lib/android-sdk-linux_x86

tmux new-session -n init-tmp -s android -d
tmux set-environment -t android PATH $ROOT/platform-tools:$ROOT/tools:$ROOT/apache-ant-1.8.2/bin:$PATH

tmux new-window -n main -t android
tmux move-window -k -s android:main -t android:init-tmp

tmux new-window -n emulator -t android
tmux new-window -n log -t android

tmux select-window -t android:main

tmux attach-session -t android
