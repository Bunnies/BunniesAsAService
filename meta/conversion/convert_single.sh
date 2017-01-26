ffmpeg -y -f gif -i "$1" -crf 18 -preset veryslow -c:v libx264 -movflags +faststart -pix_fmt yuv420p -vf "scale=trunc(iw/2)*2:trunc(ih/2)*2" "../mp4/${1%.*}.mp4"
ffmpeg -y -f gif -i "$1" -crf 18 -b:v 0 -c:v libvpx-vp9 -pix_fmt yuv420p -vf "scale=trunc(iw/2)*2:trunc(ih/2)*2" "../webm/${1%.*}.webm"
ffmpeg -y -f gif -i "$1" -q:v 1 -ss 00:00:00 -vframes 1 "../poster/${1%.*}.png"
