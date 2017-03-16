from __future__ import print_function
import predictionio
import argparse
import random


def import_events(client, num_obs):
    '''
      Generate a dataset of the form:
      y = 10 + (5 * x1) + (2 * x2)
    '''

    print("Importing data...")
    random.seed(42)

    for obs in xrange(0, num_obs):
        intercept = 10
        x1 = random.random()
        x2 = random.random()
        y = intercept + (5 * x1) + (10 * x2)

        client.create_event(
            event="$set",
            entity_type="uid",
            entity_id=str(obs),  # use the observation num as user ID
            properties={
                "x1": float(x1),
                "x2": float(x2),
                "y": float(y)
            }
        )


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description="Import sample data for regression engine")
    parser.add_argument('--access_key', default='invald_access_key')
    parser.add_argument('--url', default="http://localhost:7070")
    parser.add_argument('--obs', default=500)

    args = parser.parse_args()
    print(args)

    client = predictionio.EventClient(
        access_key=args.access_key,
        url=args.url,
        threads=5,
        qsize=500)

    import_events(client, args.obs)
