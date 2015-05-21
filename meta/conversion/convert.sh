cd gif
for GIF in *.gif
do
  ffmpeg -f gif -i "$GIF" -b 1800k -vcodec libx264 -preset slow -g 30 -pix_fmt yuv420p -vf "scale=trunc(iw/2)*2:trunc(ih/2)*2" "../mp4/${GIF%.*}.mp4"
  ffmpeg -f gif -i "$GIF" -q:v 1 -b:v 3M "../webm/${GIF%.*}.webm"
  ffmpeg -f gif -i "$GIF" -q:v 1 -ss 00:00:00 -vframes 1 "../poster/${GIF%.*}.png"
done
