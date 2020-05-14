rm *.png

mogrify -resize 800x800 -gravity center -bordercolor "#23272A" -border 112x112 -path . ../*.png
