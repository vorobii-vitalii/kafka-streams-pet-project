#!/usr/bin/zsh

IMAGE=$1

docker save $IMAGE > myimage.tar
microk8s ctr image import myimage.tar
rm myimage.tar