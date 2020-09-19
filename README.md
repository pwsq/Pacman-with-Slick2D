# Pacman Remake with Slick2D


### Instruction for installing Slick2D and LWJGL Libraries
The libraries can be downloaded as a single zip file from https://slick.ninjacave.com/. <br />

For IntelliJ IDEA windows, open project structure -> modules -> dependencies -> add JARs or directories -> choose all the files in the unzipped directory.<br />

For Eclipse windows, open project properties -> Java Build Path -> Libraries -> add external JARS -> choose all files in the unzipped directory -> expand JRE system library -> click Native library location -> add all .dll extension in the unzipped directory -> click apply.

More detailed instruction for installing the libraries can be found http://slick.ninjacave.com/wiki/index.php?title=Setting_up_Slick2D_with_Eclipse.
### Instruction for running
Upon adding the libraries in Project Structure, the game can be run from running the main method in GameStateManager class. 
The direction of Pacman is controlled by Up, down, left, right Arrow keys. You goal is to score as many points as possible by
eating dots and fruits on the map without running into the ghosts. Every time pacman runs into the ghost, the remaining lives decreases by 1
and there are 3 lives given for each game. The game levels up if you finish eating all of the dots on the map. There are totally
four maps for the first 4 levels of the game, each level gets harder with more complicated maps and more ghosts. After you reach 
level 5, the future levels stay on the most complicated map until the pacman runs out of lives. Please node that the game
enters an interesting easter egg mode if you press 'p' any time during the game. Give that a try.
When the game ends, there will be options to replay the game or see the highest scores. The High Score screen provides the historical 
high scores of the game including the standing of your last game if it made it to be one of the historical high scores.
