package com.exa.test.ai_machine;

import java.util.ArrayList;

/** 
 * <p>
 * This package implements a simplified version of the game <a href="https://en.wikipedia.org/wiki/Risk_(game)">Risk</a>. 
 * The moves are random possible moves and are not optimized in any way.
 * </p>
 * <p>
 * The {@code MinimalRisk} class provides an API consisting exclusively of static methods that accept 
 * as their first parameter the current state of the board in form of a JSON string and that return one of the following: 
 * </p>
 * <ul>
 *   <li>A JSON string representing the next state of the board</li>
 *   <li>A JSON string representing a list of countries</li>
 *   <li>A boolean</li>
 * </ul>
 * <p>
 * As all methods are static and no object state is maintained between subsequent method calls this class
 * can serve as the basis for a simple stateless web API. 
 * </p>
 * <p>
 * The implementation of the graph model and breadth-first search are based on Python code presented in 
 * <a href="https://www.edx.org/course/6-00-2x-introduction-to-computational-thinking-and-data-science-3">MITx - 6.00.2x</a>.
 * </p>
 */
public class MinimalRisk {

    /**
     * Random allocation of countries to players.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param playerA Name of player A
     * @param playerB Name of player B
     * @return JSON string representing the next state of the board (same structure as parameter {@code countryGraphJSON})
     */
    public static String initialAllocationOfCountries(String countryGraphJSON, String playerA, String playerB) {
        Board board = new Board(countryGraphJSON);
        board.initialAllocationOfCountries(playerA, playerB);
        return board.getCountryGraphJSON();
    }

    /**
     * Random allocation of extra troops before the proper game begins.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param extraTroopsEach Number of extra troops for each player
     * @return JSON string representing the next state of the board (same structure as parameter {@code countryGraphJSON})
     */
    public static String initialAllocationOfExtraTroops(String countryGraphJSON, int extraTroopsEach) {
        Board board = new Board(countryGraphJSON);
        board.initialAllocationOfExtraTroops(6);
        return board.getCountryGraphJSON();
    }

    /**
     * Random allocation of extra troops.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param player Player receiving extra troops
     * @param extraTroops Number of extra troops to be allocated
     * @return JSON string representing the next state of the board (same structure as parameter {@code countryGraphJSON})
     */
    public static String allocationOfExtraTroops(String countryGraphJSON, String player, int extraTroops) {
        Board board = new Board(countryGraphJSON);
        board.resetModified();
        board.allocationOfExtraTroops(player, extraTroops);
        return board.getCountryGraphJSON();
    }

    /**
     * Random attack by player.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param attackingPlayer Attacking player
     * @return JSON string representing the next state of the board (same structure as parameter {@code countryGraphJSON})
     */
    public static String attack(String countryGraphJSON, String attackingPlayer) {
        Board board = new Board(countryGraphJSON);
        board.resetModified();
        board.attack(attackingPlayer);
        return board.getCountryGraphJSON();
    }

    /**
     * Randomly moving troops.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param player Player moving troops
     * @return JSON string representing the next state of the board (same structure as parameter {@code countryGraphJSON})
     */
    public static String moveTroops(String countryGraphJSON, String player) {
        Board board = new Board(countryGraphJSON);
        board.resetModified();
        board.moveTroops(player);
        return board.getCountryGraphJSON();
    }
    
    /**
     * Determines whether a game is over. Implemented rule: a game is over when on player controls an entire continent (country group).
     * @param countryGraphJSON JSON string representing the current state of the board
     * @return true if one player controls a whole continent
     */
    public static boolean gameOver(String countryGraphJSON) {
        Board board = new Board(countryGraphJSON);
        return board.gameOver();
    }

    /**
     * Determines possible target countries for an attack by player {@code attackingPlayer}.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param attackingPlayer Attacking player
     * @return JSON string representing a list of countries
     */
    public static String possibleTargetCountries(String countryGraphJSON, String attackingPlayer) {
        Board board = new Board(countryGraphJSON);
        ArrayList<Node> possibleTargetCountries = board.possibleTargetCountries(attackingPlayer);
        return Board.getCountryListJSON(possibleTargetCountries);
    }

    /**
     * Determines possible target countries for an attack by player {@code attackingPlayer} from country {@code attackingCountry}.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param attackingPlayer Attacking player
     * @param attackingCountry Country the attack is to be launched from
     * @return JSON string representing a list of countries
     */
    public static String possibleTargetCountries(String countryGraphJSON, String attackingPlayer, String attackingCountry) {
        Board board = new Board(countryGraphJSON);
        ArrayList<Node> possibleTargetCountries = board.possibleTargetCountries(attackingPlayer, board.getNode(attackingCountry));
        return Board.getCountryListJSON(possibleTargetCountries);
    }

    /**
     * Determines possible attacking countries for an attack by player {@code attackingPlayer} on country {@code targetCountry}.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param attackingPlayer Attacking player
     * @param targetCountry Country to be attacked
     * @return JSON string representing a list of countries
     */
    public static String possibleAttackingCountries(String countryGraphJSON, String attackingPlayer, String targetCountry) {
        Board board = new Board(countryGraphJSON);
        ArrayList<Node> possibleAttackingCountries = board.possibleAttackingCountries(attackingPlayer, board.getNode(targetCountry));
        return Board.getCountryListJSON(possibleAttackingCountries);
    }

    /**
     * Simulates a battle between {@code attackingCountry} and {@code targetCountry}, with random outcome. 
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param attackingCountry Attacking country
     * @param targetCountry Country to be attacked
     * @return JSON string representing the next state of the board (same structure as parameter {@code countryGraphJSON})
     */
    public static String battle(String countryGraphJSON, String attackingCountry, String targetCountry) {
        Board board = new Board(countryGraphJSON);
        board.resetModified();
        board.battle(board.getNode(attackingCountry), board.getNode(targetCountry));
        return board.getCountryGraphJSON();
    }

    /**
     * Determines countries that are possible destinations for the movement of troops belonging to {@code player}.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param player Player whose troops are to be moved
     * @return JSON string representing a list of countries
     */
    public static String possibleDestinations(String countryGraphJSON, String player) {
        Board board = new Board(countryGraphJSON);
        ArrayList<Node> possibleDestinations = board.possibleDestinations(player);
        return Board.getCountryListJSON(possibleDestinations);
    }

    /**
     * Determines countries that are possible destinations for the movement of troops belonging to {@code player}, given a source country {@code sourceCountry}.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param player Player whose troops are to be moved
     * @param sourceCountry Country that troops are to be moved from
     * @return JSON string representing a list of countries
     */
    public static String possibleDestinations(String countryGraphJSON, String player, String sourceCountry) {
        Board board = new Board(countryGraphJSON);
        ArrayList<Node> possibleDestinations = board.possibleDestinations(player, board.getNode(sourceCountry));
        return Board.getCountryListJSON(possibleDestinations);
    }

    /**
     * Determines countries that are possible starting points for the movement of troops belonging to {@code player}.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param player Player whose troops are to be moved
     * @return JSON string representing a list of countries
     */
    public static String possibleSources(String countryGraphJSON, String player) {
        Board board = new Board(countryGraphJSON);
        ArrayList<Node> possibleSources = board.possibleSources(player);
        return Board.getCountryListJSON(possibleSources);
    }

    /**
     * Determines countries that are possible starting points for the movement of troops belonging to {@code player}, given a destination {@code destinationCountry}.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param player Player whose troops are to be moved
     * @param destinationCountry Country that troops are to be moved to
     * @return JSON string representing a list of countries
     */
    public static String possibleSources(String countryGraphJSON, String player, String destinationCountry) {
        Board board = new Board(countryGraphJSON);
        ArrayList<Node> possibleSources = board.possibleSources(player, board.getNode(destinationCountry));
        return Board.getCountryListJSON(possibleSources);
    }

    /**
     * Determines a list of countries that form the shortest path for the movement of troops belonging to {@code player} 
     * from a {@code sourceCountry} to a {@code destinationCountry}.
     * If there are several paths with the shortest possible length, the one that is found first is returned.
     * @param countryGraphJSON JSON string representing the current state of the board
     * @param player Player receiving extra troops
     * @param sourceCountry Country that troops are to be moved from
     * @param destinationCountry Country that troops are to be moved to
     * @return JSON string representing a list of countries
     */
    public static String shortestPath(String countryGraphJSON, String player, String sourceCountry, String destinationCountry) {
        Board board = new Board(countryGraphJSON);
        ArrayList<Node> shortestPath = board.shortestPath(board.getNode(sourceCountry), board.getNode(destinationCountry), player);
        return Board.getCountryListJSON(shortestPath);
    }

}

