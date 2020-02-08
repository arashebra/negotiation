# negotiation
Tool box for opponent modeling analysis in genius
this tool box help you to analys your opponent medeling with another opponent  modeling very easily. only thing you nead add thease packages in your Genius project folder and change only two classes of Genius.
1) in package genius.gui.actions; in class Tournament: you must change this line of code main.addTab("Tournament", new MultiTournamentPanel()); with this line main.addTab("Tournament", new MyMultiTournamentPanel());
2)in package genius.gui; in class of MainPanel, you must add this lin of code repoArea.addTab("Analysis", new extraAnalysisPanel()); exactly before than this line setJMenuBar(new MenuBar(this));
Now you can create a class that extents Analysis class and you can access every thing you need to write your own opponent modeling measurments. then you must add your compiled claas to genius and start Tournoment. Toolbox create your measurments in folder extraLog with xml type.
