# Create cerficate with the IP provided as argument

openssl req -x509 -newkey rsa:4096 -nodes -out cert.pem -keyout key.pem -days 365 -subj "/C=PT/ST=Coimbra/L=Coimbra/O=FCTUC/OU=DEI/CN=$1"

# Build Monitor Image

cd ..
sh build.sh