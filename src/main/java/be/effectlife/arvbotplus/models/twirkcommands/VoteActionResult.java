package be.effectlife.arvbotplus.models.twirkcommands;

public enum VoteActionResult {

    SUCCESSFULLY_VOTED_OR_CHANGED,          //vote is cast successfully or when a vote is successfully changed
    VOTE_ALREADY_CAST,  //user tries to vote when a vote has already been cast
    INVALID_VOTE,   //vote is not a valid option
    ALREADY_VOTED_FOR_OPTION,      //only during changevote, is when already voted for that option
    NOT_YET_VOTED     //only during changevote, is when no vote has been cast yet
}
