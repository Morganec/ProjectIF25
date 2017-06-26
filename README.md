# ProjectIF25
#README du projet IF25 :
( GitHub du projet :  https://github.com/Morganec/ProjectIF25 ) 

ETAPE 1: Mise en place CouchDB : 

- télécharger CouchDB ( http://couchdb.apache.org/ )
- Ouvrir "Admin console" de couchB (localhost:5984)
- Créer un compte admin avec :
    nom  d'utilisateur : root et mot de passe : root

/!\ garder couchDB ouvert pendant toute la durée du projet  

ETAPE 2: Mise en place de la BDD :

- Ouvrir un terminal 
- Aller dans le dossier "Projet IF25" 
où se trouve le fichier "if25_tweet.json"
- copier/coller la commande suivante : 
cdbload -d if25_tweets < if25_tweets.json

ETAPE 3 : Lancement du projet 

- Ouvrir le projet sur éclipse 
- Ajouter toutes les librairies qui se trouvent dans le dossier ZIP nommé "librairies.zip" au projet 
- Lancer "Main.java"

