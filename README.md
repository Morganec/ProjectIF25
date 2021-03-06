# Implémentation KNN

IF25 - Data mining pour les réseaux sociaux

## Procédure d'installation du projet

– Installations préalables :
➢ CouchDB - http://couchdb.apache.org/
	o Si vous n’aviez pas déjà CouchDB, créez un nouvel admin et donnez-lui les identifiants que vous voulez (Le projet utilise root et root comme identifiants par défaut mais vous pouvez le modifier)
	o Si vous aviez déjà CouchDB mais pas d’admin, créez en un nouveau aussi
➢ Node JS (Node Package Manager) – https://nodejs.org/
➢ Module Node : couch-dump - https://www.npmjs.com/package/couchdb-dump

/!\ CouchDB doit être lancé durant toute l'exécution du programme

- Clone du répertoire
```sh
$ git clone https://github.com/Morganec/ProjectIF25
```

– Récupérer le jeu de données
➢ Importer sur CouchDB le dump de la base de données
	o Créer une nouvelle BD dans Couch : if25_tweets
	o Lancer dans un terminal la commande suivante (en se plaçant dans le répertoire où se trouve le fichier JSON)
	```sh
	$ cdbload -d if25_tweets < if25_tweets.json
	```
	o Chemin vers le dump : ProjectIF25/if25_tweets.json

– Lancement du programme :
➢ Ouvrir le projet avec Eclipse (ou le logiciel que vous utilisez pour les projets Java)
➢ En cas de problème de dépendances avec les librairies elles sont toutes dans le zip ProjectIF25/librairies.zip
➢ Dans le fichier Main.java modifier si nécessaire les paramètres de la méthode KNN pour indiquer vos données d’accès à CouchDB
➢ Lancer Main.java - les résultats s’affichent dans la console
