#!/bin/bash

pdflatex -halt-on-error requisitos-tecnicos-es.tex
pdflatex -halt-on-error requisitos-tecnicos-es.tex
pdflatex -halt-on-error requisitos-tecnicos-es.tex

rm *.log > /dev/null 2>&1 
rm *.out > /dev/null 2>&1 
rm *.aux > /dev/null 2>&1 
rm *.toc > /dev/null 2>&1 
