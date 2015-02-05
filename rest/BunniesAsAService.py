from flask import Flask
from flask.ext import restful
from flask_limiter import Limiter

import random

app = Flask(__name__)
api = restful.Api(app)
limiter = Limiter(app, global_limits = ["1 per second"])

bunny_gifs = { }

for bunny_id in range(1, 37):
    bunny_gifs[str(bunny_id)] = 'http://bunnies.io/bunnies/' + str(bunny_id) + '.gif'

def isIDSane(id):
    try:
        s = id.decode('ascii')
        if len(s) <= 8 and s.isalnum():
            return True
    except UnicodeDecodeError:
        return False

    return False

class BunnyGIF(restful.Resource):
    def get(self, specified_bunny_id):
        if not isIDSane(specified_bunny_id):
            return { 'error': 'Bad ID format' }, 400

        if specified_bunny_id in bunny_gifs:
            return { specified_bunny_id: bunny_gifs[specified_bunny_id] }

        return { 'error': 'Bunny ID not found' }, 400

class RandomBunnyGIF(restful.Resource):
    def get(self):
        bunny_id_list = random.sample(bunny_gifs, 1)
        if bunny_id_list is not None:
            random_bunny_id = bunny_id_list[0]
            return { random_bunny_id: bunny_gifs[random_bunny_id] }
        else:
            return { 'error': 'Failed to select a random bunny GIF' }, 500

api.add_resource(RandomBunnyGIF, '/v1/gif/')
api.add_resource(BunnyGIF, '/v1/gif/<string:specified_bunny_id>')

if __name__ == '__main__':
    app.run(debug=True)