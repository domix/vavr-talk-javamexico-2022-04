#!/bin/sh

psql "port=5434 host=localhost password='password' dbname=postgres user=admin" -c 'create database investing;'
