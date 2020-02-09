# negotiation
Opponent Model Analysis in Genius
This tool box is implemented for opponent mode analysis in Genius. It help you to analyze your opponent modeling and compare it with the other models. The only thing you need is to add these packages in your Genius project folder and change the following two classes of Genius.
1.	In package genius.gui.actions, in class Tournament, you must change this line of code:
 main.addTab("Tournament", new MultiTournamentPanel()); 
with the following line:
 main.addTab("Tournament", new MyMultiTournamentPanel());.
2.	In package genius.gui; in class of MainPanel, just find the line containing:
setJMenuBar(new MenuBar(this));.
Then add the following code just before it: 
repoArea.addTab("Analysis", new extraAnalysisPanel()); 
3.	Now you can create a class that extents Analysis class and you can access everything you need to write your own opponent modeling measurements. Then you must add your compiled class to genius and start Tournament. Toolbox create your measurements in folder extra Log with xml type.


