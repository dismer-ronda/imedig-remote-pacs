#!/bin/bash

pdflatex -halt-on-error manual-usuario-en.tex
pdflatex -halt-on-error manual-usuario-en.tex
pdflatex -halt-on-error manual-usuario-en.tex

rm *.log > /dev/null 2>&1 
rm *.out > /dev/null 2>&1 
rm *.aux > /dev/null 2>&1 
rm *.toc > /dev/null 2>&1 
