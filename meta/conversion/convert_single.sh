ffmpeg -f gif -i "$1" -b:v 1800k -vcodec libx264 -preset slow -g 30 -pix_fmt yuv420p -vf "scale=trunc(iw/2)*2:trunc(ih/2)*2" "../mp4/${1%.*}.mp4"
ffmpeg -f gif -i "$1" -q:v 1 -b:v 3M "../webm/${1%.*}.webm"
ffmpeg -f gif -i "$1" -q:v 1 -ss 00:00:00 -vframes 1 "../poster/${1%.*}.png"
