from flask import Flask, request, redirect
from flask.ext import restful
from flask_limiter import Limiter
from werkzeug.contrib.fixers import ProxyFix
import logging

import random

app = Flask(__name__)
api = restful.Api(app)
limiter = Limiter(app, global_limits=["5 per second"])
app.wsgi_app = ProxyFix(app.wsgi_app)


@app.before_first_request
def setup_logging():
    if not app.debug:
        # In production mode, add log handler to sys.stderr.
        app.logger.addHandler(logging.StreamHandler())
        app.logger.setLevel(logging.INFO)


bunny_gifs = {}
bunny_mp4s = {}
bunny_webms = {}
bunny_posters = {}
number_random_served = 0
number_specific_served = 0

for existing_bunny_id in range(1, 60):
    bunny_gifs[str(existing_bunny_id)] = 'https://media.bunnies.io/gif/' + str(existing_bunny_id) + '.gif'
    bunny_mp4s[str(existing_bunny_id)] = 'https://media.bunnies.io/mp4/' + str(existing_bunny_id) + '.mp4'
    bunny_webms[str(existing_bunny_id)] = 'https://media.bunnies.io/webm/' + str(existing_bunny_id) + '.webm'
    bunny_posters[str(existing_bunny_id)] = 'https://media.bunnies.io/poster/' + str(existing_bunny_id) + '.png'

def is_bunny_id_sane(bunny_id):
    try:
        s = bunny_id.decode('ascii')
        if len(s) <= 8 and s.isalnum():
            return True
    except UnicodeDecodeError:
        return False

    return False

def total_served():
    return number_random_served + number_specific_served


class BunnyGIF(restful.Resource):
    def get(self, specified_bunny_id):
        if not is_bunny_id_sane(specified_bunny_id):
            app.logger.info("Bad ID format from " + request.remote_addr)
            return {'error': 'Bad ID format'}, 400

        if specified_bunny_id in bunny_gifs:
            app.logger.info("Serving specific bunny" + str(specified_bunny_id) + " to " + request.remote_addr)
            global number_specific_served
            number_specific_served += 1
            return {'location': bunny_gifs[specified_bunny_id],
                    'location_mp4': bunny_mp4s[specified_bunny_id],
                    'location_webm': bunny_webms[specified_bunny_id],
                    'location_poster': bunny_posters[specified_bunny_id],
                    'id': specified_bunny_id,
                    'specifics_served': number_specific_served,
                    'total_served': total_served()}

        app.logger.info("404 bunny from " + request.remote_addr)
        return {'error': 'Bunny ID not found'}, 400


class RandomBunnyGIF(restful.Resource):
    def get(self):
        bunny_id_list = random.sample(bunny_gifs, 1)
        if bunny_id_list is not None:
            random_bunny_id = bunny_id_list[0]
            app.logger.info("Serving random bunny " + str(random_bunny_id) + " to " + request.remote_addr)
            global number_random_served
            number_random_served += 1
            return {'location': bunny_gifs[random_bunny_id],
                    'location_mp4': bunny_mp4s[random_bunny_id],
                    'location_webm': bunny_webms[random_bunny_id],
                    'location_poster': bunny_posters[random_bunny_id],
                    'id': random_bunny_id,
                    'randoms_served': number_random_served,
                    'total_served': total_served()}
        else:
            app.logger.info("Failed to serve a random bunny to " + request.remote_addr)
            return {'error': 'Failed to select a random bunny GIF'}, 500

class RedirectBunnyGIF(restful.Resource):
    def get(self):
        bunny_id_list = random.sample(bunny_gifs, 1)
        if bunny_id_list is not None:
            random_bunny_id = bunny_id_list[0]
            app.logger.info("Serving redirected random bunny " + str(random_bunny_id) + " to " + request.remote_addr)
            global number_random_served
            number_random_served += 1
            return redirect(bunny_gifs[random_bunny_id], code=302)
        else:
            app.logger.info("Failed to serve a redirected random bunny to " + request.remote_addr)
            return {'error': 'Failed to select a redirected random bunny GIF'}, 500

api.add_resource(RandomBunnyGIF, '/v1/gif/')
api.add_resource(BunnyGIF, '/v1/gif/<string:specified_bunny_id>')
api.add_resource(RedirectBunnyGIF, '/v1/gif/random.gif')

if __name__ == '__main__':
    app.run(debug=False, port=5050)
