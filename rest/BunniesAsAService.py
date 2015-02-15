from flask import Flask
from flask.ext import restful
from flask_limiter import Limiter

import random

app = Flask(__name__)
api = restful.Api(app)
limiter = Limiter(app, global_limits=["5 per second"])

bunny_gifs = {}

for existing_bunny_id in range(1, 51):
    bunny_gifs[str(existing_bunny_id)] = 'https://bunnies.io/bunnies/' + str(existing_bunny_id) + '.gif'


def is_bunny_id_sane(bunny_id):
    try:
        s = bunny_id.decode('ascii')
        if len(s) <= 8 and s.isalnum():
            return True
    except UnicodeDecodeError:
        return False

    return False


class BunnyGIF(restful.Resource):
    def get(self, specified_bunny_id):
        if not is_bunny_id_sane(specified_bunny_id):
            return {'error': 'Bad ID format'}, 400

        if specified_bunny_id in bunny_gifs:
            return {'location': bunny_gifs[specified_bunny_id], 'id': specified_bunny_id}

        return {'error': 'Bunny ID not found'}, 400


class RandomBunnyGIF(restful.Resource):
    def get(self):
        bunny_id_list = random.sample(bunny_gifs, 1)
        if bunny_id_list is not None:
            random_bunny_id = bunny_id_list[0]
            return {'location': bunny_gifs[random_bunny_id], 'id': random_bunny_id}
        else:
            return {'error': 'Failed to select a random bunny GIF'}, 500

api.add_resource(RandomBunnyGIF, '/v1/gif/')
api.add_resource(BunnyGIF, '/v1/gif/<string:specified_bunny_id>')

if __name__ == '__main__':
    app.run(debug=False, port=5050)