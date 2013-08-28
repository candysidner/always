package edu.wpi.always.srummy.game;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import edu.wpi.always.client.Message;
import edu.wpi.sgf.logic.GameLogicState;



/**
 * A class representing the Rummy game state.
 * 
 * Note: This Package has all the methods necessary for playing
 * the Rummy card game, but in the current implementation, it is 
 * being synched with a .Net part. One can use the methods in 
 * this class for 'driving' the game, but currently, the 'state' 
 * is being sent over socket and synchState() method sets the variables.
 * One potential usage of the methods is in 'planning' the game ahead 
 * and evaluating different paths or other possible usages with SGF.
 * 
 * @author Morteza Behrooz
 * @version 1.3
 */

public class GameState extends GameLogicState {

	private Deck stock;
	private Deck discard;

	private Map<Player, List<Meld>> playersMelds;
	private Map<Player, List<Card>> playersCards;

	private GamePhase currentPhase;
	private Card theCardJustDrawn;
	private ErrMsg errMsg;

	public Card getCardJustDrawn() 
	{return theCardJustDrawn;}

	public Deck getStock() 
	{return stock;}

	public Deck getDiscard() 
	{return discard;}

	public List<Meld> getMelds(Player player)
	{return playersMelds.get(player);}

	public List<Meld> getAgentMelds()
	{return getMelds(Player.Agent);}

	public List<Meld> getHumanMelds()
	{return getMelds(Player.Human);}

	public void setAgentMelds(List<Meld> meldingCards)
	{playersMelds.put(Player.Agent, meldingCards);}

	public void setHumanMelds(List<Meld> meldingCards)
	{playersMelds.put(Player.Human, meldingCards);}

	public List<Card> getCards(Player player) 
	{return playersCards.get(player);}

	public List<Card> getAgentCards()
	{return getCards(Player.Agent);}

	public List<Card> getHumanCards()
	{return getCards(Player.Human);}

	public GamePhase getCurrentPhase()
	{return currentPhase;}

	public ErrMsg currentError()
	{return errMsg;}

	public boolean gameIsOver(){
		return currentPhase == GamePhase.AgentWon 
				|| currentPhase == GamePhase.HumanWon;
	}

	public GameState(){
		generateInitialGameStateAndDealCards();
	}

	public GameState(GamePhase startingPhase){
		generateInitialGameStateAndDealCards();
		currentPhase = startingPhase;
	}

	public GameState(List<Card> agentCards){
		generateInitialPhaseAndShuffleStock(agentCards);
		dealCardsTo(Player.Human);
		for(Card eachCard : agentCards)
			addToPlayerCards(Player.Agent, eachCard);
	}

	public GameState(List<Card> agentCards, GamePhase aPhase){ 
		this(agentCards); 
		currentPhase = aPhase; 
	}

	public GameState(List<Card> agentCards, List<Meld> agentMelds){

		List<Card> ignoreCard = new ArrayList<Card>();

		for(Card eachCard : agentCards)
			ignoreCard.add(eachCard);

		generateInitialPhaseAndShuffleStock(ignoreCard);
		playersMelds.put(Player.Agent, agentMelds);//true if used only in constructor (adds, does not replace) check
		dealCardsTo(Player.Human);

		for(Card eachCard : agentCards)
			addToPlayerCards(Player.Agent, eachCard);

	}

	public GameState(List<Card> agentCards, List<Meld> agentMelds, GamePhase aPhase){
		this(agentCards, agentMelds);
		currentPhase = aPhase;
	}

	private void generateInitialGameStateAndDealCards() {
		generateInitialPhaseAndShuffleStock();
		dealCardsToPlayers();
	}

	private void generateInitialPhaseAndShuffleStock() {
		generateInitialPhaseAndShuffleStock(new ArrayList<Card>());
	}

	private void generateInitialPhaseAndShuffleStock(List<Card> agentCards) {
		stock = new Deck();
		discard = new Deck();
		playersCards.put(Player.Agent, new ArrayList<Card>());
		playersCards.put(Player.Human, new ArrayList<Card>());
		playersMelds.put(Player.Agent, new ArrayList<Meld>());
		playersMelds.put(Player.Human, new ArrayList<Meld>());
		currentPhase = GamePhase.AgentDraw;
		errMsg = ErrMsg.NoError;
		stock.create();
		discard.addCard(stock.pop());
	}

	private void dealCardsToPlayers() {
		dealCardsTo(Player.Agent);
		dealCardsTo(Player.Human);
	}

	private void dealCardsTo(Player aPlayer) {
		for(int index = 0; index < 10; index++){
			Card cardsBeingDealt = stock.pop();
			cardsBeingDealt.faceItUp();
			addToPlayerCards(aPlayer, cardsBeingDealt);
		}
	}

	public void discardCard(Player thePlayer, Card aCard){

		if(!canDiscard(thePlayer)){
			errMsg = ErrMsg.NotPlayerTurn;
			throw new IllegalArgumentException(
					"Not " + thePlayer.name() + "'s turn!");
		}

		if(!playerHasTheCard(thePlayer, aCard)){
			errMsg = ErrMsg.NotPlayerCard;
			throw new IllegalArgumentException(thePlayer.name() 
					+ " does not have " + aCard.toString());
		}

		if(aCard.equals(theCardJustDrawn)){
			errMsg = ErrMsg.CannotDiscardThis;
			throw new IllegalArgumentException(thePlayer.name() 
					+ "cannot discard a card which was just drawn!");
		}

		removePlayerCard(thePlayer, aCard);

		discard.addCard(aCard);

		GamePhase newPhase = (thePlayer == Player.Agent) ? 
				(GamePhase.AgentDraw) : (GamePhase.HumanDraw);

				afterActStuff(new DiscardMove(thePlayer, aCard), newPhase);

				errMsg = ErrMsg.NoError;
	}

	public void drawCard(Player thePlayer, Pile aPile){

		if(!canDraw(thePlayer)){
			errMsg = ErrMsg.NotPlayerTurn;
			throw new IllegalArgumentException("It is not " 
					+ thePlayer.name() + "'s turn!");
		}

		if(isPileEmpty(aPile))
			throw new IllegalArgumentException("The pile " 
					+ aPile.name() + "is empty.");

		Card drawnCard = popFromPile(aPile);

		addToPlayerCards(thePlayer, drawnCard);

		if(aPile == Pile.Discard)
			theCardJustDrawn = drawnCard;
		else 
			theCardJustDrawn = null;

		if(aPile == Pile.Stock && isPileEmpty(aPile)){
			Card theCardTopOnDiscard = discard.pop();
			while(discard.size() > 0){
				stock.addCard(discard.pop());
				stock.shuffleCards();
			}
			discard.addCard(theCardTopOnDiscard);
		}

		errMsg = ErrMsg.NoError;

		GamePhase newPhase = (thePlayer == Player.Agent) ? 
				(GamePhase.AgentMeldLayDiscard) : (GamePhase.HumanMeldLayDiscard);

				afterActStuff(new DrawMove(thePlayer, aPile), newPhase);		

	}

	public void layOff(Player thePlayer, Card addingCard, Meld aMeld){

		if(!canMeldLayOff(thePlayer)){
			errMsg = ErrMsg.NotPlayerTurn;
			throw new IllegalArgumentException("It is not " 
					+ thePlayer.name() + "'s turn!");
		}

		if(!playerHasTheCard(thePlayer, addingCard)){
			errMsg = ErrMsg.NotPlayerCard;
			throw new IllegalArgumentException(thePlayer.name() 
					+ " does not have " + addingCard.toString());
		}

		if(aMeld.canAlsoHaveThis(addingCard))
			aMeld.addThis(addingCard);
		else{
			errMsg = ErrMsg.InvalidLayoff;
			throw new IllegalArgumentException("Cannot add " 
					+ addingCard.toString() + "to this meld");
		}

		removePlayerCard(thePlayer, addingCard);

		LayoffMove layOffMove = new LayoffMove(thePlayer, addingCard, aMeld);

		afterActStuff(layOffMove);

		errMsg = ErrMsg.NoError;

	}

	public void meld(Player thePlayer, List<Card> meldingCards){

		if(!canMeldLayOff(thePlayer)){
			errMsg = ErrMsg.NotPlayerTurn;
			throw new IllegalArgumentException("It is not " 
					+ thePlayer.name() + "'s turn!");
		}

		if(!playerHasAllTheCards(thePlayer, meldingCards)){
			errMsg = ErrMsg.NotPlayerCard;
			throw new IllegalArgumentException(thePlayer.name() 
					+ " does not have at least " +
					"one the cards being melded");
		}


		Meld aMeld = new Meld();

		if(aMeld.isValid(meldingCards))
			aMeld = new Meld(meldingCards);

		else{
			errMsg = ErrMsg.InvalidMeld;
			throw new IllegalArgumentException(
					"Cannot form a meld from these cards.");
		}

		removePlayerCards(thePlayer, meldingCards);

		addtoPlayerMelds(thePlayer, aMeld);

		MeldMove aMeldMove = new MeldMove(thePlayer, new Meld(meldingCards));

		afterActStuff(aMeldMove);

	}

	public void meld(Player thePlayer, Meld aMeld){
		meld(thePlayer, aMeld.getCards());
	}

	private Card popFromPile(Pile aPile){

		if(aPile == Pile.Stock)
			return stock.pop();
		return discard.pop();

	}

	private void addToPlayerCards(Player thePlayer, Card aCard){

		getCards(thePlayer).add(aCard);

	}

	private void addtoPlayerMelds(Player thePlayer, Meld addingMeld) {

		getMelds(thePlayer).add(addingMeld);

	}

	private void removePlayerCard(Player thePlayer, Card aCard) {

		getCards(thePlayer).remove(aCard);

	}

	private void removePlayerCards(Player thePlayer, List<Card> someCards) {

		for(Card eachCard: someCards)
			removePlayerCard(thePlayer, eachCard);

	}

	private boolean canDiscard(Player thePlayer) {

		if(thePlayer == Player.Agent)
			return currentPhase == GamePhase.AgentMeldLayDiscard;
		return currentPhase == GamePhase.HumanMeldLayDiscard;

	}

	private boolean canDraw(Player thePlayer){

		if(thePlayer == Player.Agent)
			return currentPhase 
					== GamePhase.AgentDraw;

		return currentPhase 
				== GamePhase.HumanDraw;

	}

	private boolean canMeldLayOff(Player thePlayer){

		if(thePlayer == Player.Agent)
			return currentPhase 
					== GamePhase.AgentMeldLayDiscard;
		return currentPhase 
				== GamePhase.HumanMeldLayDiscard;

	}

	public boolean isItPlayerTurn(Player thePlayer){

		if(thePlayer == Player.Agent)
			return (currentPhase == GamePhase.AgentDraw
			|| currentPhase == GamePhase.AgentMeldLayDiscard);
		else if(thePlayer == Player.Human)
			return (currentPhase == GamePhase.HumanDraw
			|| currentPhase == GamePhase.HumanMeldLayDiscard);
		return false;

	}

	private boolean isPileEmpty(Pile aPile){

		if(aPile == Pile.Discard)
			return discard.isEmpty();
		else if(aPile == Pile.Stock)
			return stock.isEmpty();
		return true;

	}

	private boolean playerHasTheCard(Player thePlayer, Card aCard){

		if(thePlayer == Player.Agent && !getAgentCards().contains(aCard))
			return false;
		else if(thePlayer == Player.Human &&!getHumanCards().contains(aCard))
			return false;
		return true;

	}

	private boolean playerHasAllTheCards(Player thePlayer,
			List<Card> meldingCards) {

		if(thePlayer == Player.Agent && !getAgentCards()
				.containsAll(meldingCards))
			return false;

		else if(thePlayer == Player.Human &&!getHumanCards()
				.containsAll(meldingCards))
			return false;

		return true;

	}

	private void afterActStuff(Move aMove) {

		afterActStuff(aMove, null);

	}

	private void afterActStuff(Move aMove, GamePhase newPhase) {

		if(getAgentCards().size() == 0)
			changePhaseTo(GamePhase.AgentWon);

		if(getHumanCards().size() == 0)
			changePhaseTo(GamePhase.HumanWon);

		if(!gameIsOver() && newPhase != null)
			changePhaseTo(newPhase);

	}

	private void changePhaseTo(GamePhase newPhase) {

		if(gameIsOver()) 
			return;
		currentPhase = newPhase; 

		//these are needed for the framework
		this.agentWins = currentPhase
				.equals(GamePhase.AgentWon);
		this.userWins = currentPhase
				.equals(GamePhase.HumanWon);

	}

	/**
	 * This method 'synchs' the state received from .Net
	 * @see Javadoc for GameState
	 * @throws Exception for not having the correct format in json
	 */
	public void synchGame(Message gameStateAsMessage) throws Exception{

		JsonObject gameStateJsonObj = 
				gameStateAsMessage.getBody();

		if(gameStateJsonObj == null ||
				!gameStateJsonObj.has("agentCards") ||
				!gameStateJsonObj.has("humanCards") ||
				!gameStateJsonObj.has("stockCards") ||
				!gameStateJsonObj.has("discardCards") ||
				!gameStateJsonObj.has("agentMelds") ||
				!gameStateJsonObj.has("humanMelds"))
			throw new Exception(
					"State sending from .Net is not in the right format or is incomplete.");

		String agentCardsAsString = gameStateJsonObj.get("agentCards").getAsString();
		String humanCardsAsString = gameStateJsonObj.get("humanCards").getAsString();
		String stockCardsAsString = gameStateJsonObj.get("stockCards").getAsString();
		String discardCardsAsString = gameStateJsonObj.get("discardCards").getAsString();
		String agentMeldsAsString = gameStateJsonObj.get("agentMelds").getAsString();
		String humanMeldsAsString = gameStateJsonObj.get("humanMelds").getAsString();

		List<Card> agentCardsTemp = new ArrayList<Card>();
		List<Card> humanCardsTemp = new ArrayList<Card>();
		List<Card> stockCardsTemp = new ArrayList<Card>();
		List<Card> discardCardsTemp = new ArrayList<Card>();
		List<Meld> agentMeldsTemp = new ArrayList<Meld>();
		List<Meld> humanMeldsTemp = new ArrayList<Meld>();
		int stockIndex = 0, discardIndex = 0; 
		
		List<Card> meldCardsTemp = new ArrayList<Card>();

		for(String eachCardAsString : agentCardsAsString.split("/"))
			agentCardsTemp.add(new Card(eachCardAsString));

		for(String eachCardAsString : humanCardsAsString.split("/"))
			humanCardsTemp.add(new Card(eachCardAsString));

		for(String eachCardAsString : stockCardsAsString.split("--")[0].split("/"))
			stockCardsTemp.add(new Card(eachCardAsString));
		stockIndex = Integer.valueOf(stockCardsAsString.split("--")[1]);
		
		for(String eachCardAsString : discardCardsAsString.split("--")[0].split("/"))
			discardCardsTemp.add(new Card(eachCardAsString));
		discardIndex = Integer.valueOf(discardCardsAsString.split("--")[1]);
		
		for(String eachMeldAsString : agentMeldsAsString.split("--")){
			meldCardsTemp.clear();
			for(String eachCardAsString : eachMeldAsString.split("/")){
				meldCardsTemp.add(new Card(eachCardAsString));
				discardCardsTemp.add(new Card(eachMeldAsString));
			}
			agentMeldsTemp.add(new Meld(meldCardsTemp));
		}

		for(String eachMeldAsString : humanMeldsAsString.split("--")){
			meldCardsTemp.clear();
			for(String eachCardAsString : eachMeldAsString.split("/")){
				meldCardsTemp.add(new Card(eachCardAsString));
				discardCardsTemp.add(new Card(eachMeldAsString));
			}
			humanMeldsTemp.add(new Meld(meldCardsTemp));
		}
		
		playersCards.clear();
		playersCards.put(Player.Agent, agentCardsTemp);
		playersCards.put(Player.Human, humanCardsTemp);
		
		playersMelds.clear();
		playersMelds.put(Player.Agent, agentMeldsTemp);
		playersMelds.put(Player.Human, humanMeldsTemp);
		
		stock.synch(stockCardsTemp, stockIndex); //get index in JSON (e.g. ... -- index)
		discard.synch(discardCardsTemp, discardIndex); //get index
		
	}

}