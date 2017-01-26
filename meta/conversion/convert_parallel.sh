cd gif

find ./ -type f -name '*.gif' | parallel -j 16 --eta ../convert_single.sh {}
