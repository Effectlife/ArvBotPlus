package be.effectlife.arvbotplus.models.twirkcommands;

public enum VoteActionResult {

    SUCCESSFULLY_VOTED_OR_CHANGED,
    VOTE_ALREADY_CAST,
    MULTIVOTE_ALREADY_CAST,
    INVALID_VOTE,
    INVALID_VOTE_NEW,
    INVALID_VOTE_OLD,
    IMPROPER_CHANGE_MULTIVOTE,
    ALREADY_VOTED_FOR_OPTION,
    NOT_YET_VOTED,
    NOT_VOTED_FOR,
    REMOVED_VOTE;
}
