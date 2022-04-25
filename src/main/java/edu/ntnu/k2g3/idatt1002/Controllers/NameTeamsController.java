package edu.ntnu.k2g3.idatt1002.Controllers;

import edu.ntnu.k2g3.idatt1002.FileHandling.PadelFileWriter;
import edu.ntnu.k2g3.idatt1002.Player;
import edu.ntnu.k2g3.idatt1002.Team;
import edu.ntnu.k2g3.idatt1002.Tournament;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class NameTeamsController{

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private ListView<String> listOfTeams;
    @FXML
    private Button startButton, addButton;

    @FXML
    private TextField playerNameField, teamNameField, playerOne, playerTwo;

    @FXML
    private Label teamChooser, teamNameLabel;

    @FXML
    private ChoiceBox<Integer> numberOfTeamsChoiceBox;
    @FXML
    private Button confirm;

    @FXML
    private AnchorPane numberOfTeamsAnchor, doublesAnchor;

    @FXML
    private Rectangle rectangle;

    private Tournament tournament;


    public void initialize() {
        numberOfTeamsChoiceBox.getItems().setAll(4,8,16);
        confirm.setDisable(true);
        numberOfTeamsAnchor.setVisible(true);
        startButton.setVisible(false);

        confirm.setOnAction((event) -> {
            numberOfTeamsAnchor.setVisible(false);
            createTeamList(numberOfTeamsChoiceBox.getValue());
        });

        addButton.setOnAction((event) -> {
            if (tournament.isDoubles()){
                addTeamToListOfTeams(teamNameField);
                playerOne.clear();
                playerTwo.clear();
                playerOne.setPromptText("Firstname + lastname");
                playerTwo.setPromptText("Firstname + lastname");
            }else{
                addTeamToListOfTeams(playerNameField);
            }
        });
    }

    @FXML
    public void teamsSelected() {
        confirm.setDisable(false);
    }

    public void setTournament(Tournament tournament){
        this.tournament = tournament;
        if (tournament.isDoubles()){
            teamChooser.setText("Choose a team from the dropdown menu:");
            teamNameLabel.setText("Enter team name:");
            playerNameField.setPromptText("Team name...");
            addButton.setText("Add team");
            doublesAnchor.setVisible(true);
            playerNameField.setVisible(false);
        }
    }

    public void createTeamList(int numberOfTeams) {
        if(tournament.isDoubles()){
            for(int i = 0; i < numberOfTeams; i++){
                choiceBox.getItems().add("Team" + (i + 1));}
        }else {
            for(int i = 0; i < numberOfTeams; i++){
                choiceBox.getItems().add("Player" + (i + 1));}
        }}

    @FXML
    public void chosenTeam() {
        //playerNameField.setText(choiceBox.getValue()); //set prompt tekst? da må man skrive navn?
        addButton.setDisable(false);
    }

    @FXML
    public void addTeamToListOfTeams(TextField nameField) {
        try{
        tournament.addTeam(createTeam());

        listOfTeams.getItems().add(nameField.getText());
        choiceBox.getItems().remove(choiceBox.getSelectionModel().getSelectedItem());
        choiceBox.valueProperty().setValue(null);
        nameField.clear();
        addButton.setDisable(true);

        teamNameField.setPromptText("Team name");
        playerNameField.setPromptText("Firstname + lastname");

        if(choiceBox.getItems().size() == 0) {
            setAllTeamsAdded();
        }
        }catch (IllegalArgumentException e){
            playerNameField.clear();
            teamNameField.clear();
            playerNameField.setPromptText(e.getMessage());
            teamNameField.setPromptText(e.getMessage());
        }
    }

    private void setAllTeamsAdded(){
        startButton.setVisible(true);
        rectangle.setVisible(true);
        teamChooser.setText("All players have been added.");
        if (tournament.isDoubles()){
            teamChooser.setText("All teams have been added.");
        }
        System.out.println(tournament.getFirtsMatch());
    }

    private Team createTeam()throws IllegalArgumentException{
        if (tournament.isDoubles()){
            return new Team(teamNameField.getText(), createPlayer(playerOne), createPlayer(playerTwo));
        }else return new Team(createPlayer(playerNameField));
    }

    private Player createPlayer(TextField player)throws IllegalArgumentException {
        String[] list = player.getText().split(" ");
        if (list.length < 2){throw new IllegalArgumentException("Player needs a full name!");}
        String surname = list[list.length - 1];
        String firstname = player.getText().replace(surname, "");
        return new Player(firstname, surname);
    }


    @FXML
    public void goToTournament(ActionEvent event) throws IOException {
        FXMLLoader loader;
        Parent root;
        ArrayList<String> listOfTeamNames = new ArrayList<>(listOfTeams.getItems());
        PadelFileWriter.writeTeamNamesToFile("src/main/resources/edu/ntnu/k2g3/idatt1002/tournamentFiles/listOfTeamNames.txt", listOfTeamNames);

        switch (listOfTeams.getItems().size()) {
            case 4 -> {
                loader = new FXMLLoader(getClass().getResource("/edu/ntnu/k2g3/idatt1002/tournament.fxml"));
                root = loader.load();
                TournamentController controller = loader.getController();
                controller.setTournament(this.tournament);
            }
            case 8 -> {
                loader = new FXMLLoader(getClass().getResource("/edu/ntnu/k2g3/idatt1002/tournament8.fxml"));
                root = loader.load();
                Tournament8Controller controller8 = loader.getController();
                controller8.setTournament(this.tournament);
            }
            case 16 -> {
                loader = new FXMLLoader(getClass().getResource("/edu/ntnu/k2g3/idatt1002/tournament16.fxml"));
                root = loader.load();
                Tournament16Controller controller16 = loader.getController();
                controller16.setTournament(this.tournament);
            }
            default -> throw new IllegalArgumentException("Number of teams are out of range.");
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
