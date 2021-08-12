package be.effectlife.arvbotplus.loading;

public enum MessageKey {
    SCENE_BATTLE_BUTTON_ADD("scene.battle.button.add"),
    SCENE_BATTLE_BUTTON_REMOVE("scene.battle.button.remove"),
    SCENE_BATTLE_BUTTON_CLEAR("scene.battle.button.clear"),
    SCENE_BATTLE_TEXT_VAL_1("scene.battle.text.val1"),
    SCENE_BATTLE_TEXT_VAL_2("scene.battle.text.val2"),

    SCENE_DICE_TEXT_ROLL("scene.dice.text.roll"),
    SCENE_DICE_TEXT_D("scene.dice.text.d"),
    SCENE_DICE_BUTTON_ROLL("scene.dice.button.roll"),

    SCENE_INVENTORY_MENU_FILE("scene.inventory.menu.file"),
    SCENE_INVENTORY_MENU_FILE_SAVE("scene.inventory.menu.file.save"),
    SCENE_INVENTORY_MENU_FILE_LOAD("scene.inventory.menu.file.load"),
    SCENE_INVENTORY_MENU_FILE_CLOSE("scene.inventory.menu.file.close"),
    SCENE_INVENTORY_MENU_TOOLS("scene.inventory.menu.tools"),
    SCENE_INVENTORY_MENU_TOOLS_POLLS("scene.inventory.menu.tools.polls"),
    SCENE_INVENTORY_MENU_TOOLS_DICE("scene.inventory.menu.tools.dice"),
    SCENE_INVENTORY_MENU_TOOLS_BATTLE("scene.inventory.menu.tools.battle"),
    SCENE_INVENTORY_MENU_HELP("scene.inventory.menu.help"),
    SCENE_INVENTORY_MENU_HELP_ABOUT("scene.inventory.menu.help.about"),
    SCENE_INVENTORY_TEXT_CHARNAME("scene.inventory.text.charname"),
    SCENE_INVENTORY_TEXT_PLACEHOLDER("scene.inventory.text.placeholder"),
    SCENE_INVENTORY_TEXT_ITEMSANDARTIFACTS("scene.inventory.text.itemsandartifacts"),
    SCENE_INVENTORY_TEXT_CLUESANDNOTES("scene.inventory.text.cluesandnotes"),

    SCENE_POLLS_TEXT_CONNECTEDTO("scene.polls.text.connectedto"),
    SCENE_POLLS_DEFAULTQUESTION("scene.polls.defaultquestion"),
    SCENE_POLLS_TEXT_OPTIONS("scene.polls.text.options"),
    SCENE_POLLS_BUTTON_CLEARALL("scene.polls.button.clearall"),
    SCENE_POLLS_BUTTON_OPEN("scene.polls.button.open"),
    SCENE_POLLS_BUTTON_CLOSE("scene.polls.button.close"),
    SCENE_POLLS_BUTTON_LASTCALL("scene.polls.button.lastcall"),

    WIDGET_POLLS_BUTTON_CLEAR("widget.polls.button.clear"),

    WIDGET_QUICKPOLL_BUTTON_CLEAR("widget.quickpoll.button.clear"),
    WIDGET_QUICKPOLL_BUTTON_OPEN("widget.quickpoll.button.open"),
    WIDGET_QUICKPOLL_BUTTON_CLOSE("widget.quickpoll.button.close"),
    WIDGET_QUICKPOLL_BUTTON_LASTCALL("widget.quickpoll.button.lastcall"),

    TWIRK_PATTERN_PREFIX("twirk.pattern.prefix"),
    TWIRK_PATTERN_COMMAND_ABP("twirk.pattern.command.abp"),
    TWIRK_PATTERN_COMMAND_CHANGEVOTE("twirk.pattern.command.changevote"),
    TWIRK_PATTERN_COMMAND_VOTE("twirk.pattern.command.vote"),
    TWIRK_PATTERN_COMMAND_CONVERSION("twirk.pattern.command.conversion"),
    TWIRK_PATTERN_COMMAND_QUESTION("twirk.pattern.command.question"),
    TWIRK_MESSAGE_STARTUP("twirk.message.startup"),
    TWIRK_MESSAGE_ABP_HELP("twirk.message.abp.help"),

    TWIRK_MESSAGE_POLL_LASTCALL("twirk.message.vote.lastcall"),
    TWIRK_MESSAGE_POLL_TEMPLATE_QUESTION("twirk.message.poll.template.question"),
    TWIRK_MESSAGE_POLL_TEMPLATE_DRAW("twirk.message.poll.template.draw"),
    TWIRK_MESSAGE_POLL_TEMPLATE_WIN("twirk.message.poll.template.win"),
    TWIRK_MESSAGE_QUICKPOLL_TEMPLATE_QUESTION("twirk.message.quickpoll.template.question"),
    TWIRK_MESSAGE_QUICKPOLL_TEMPLATE_DRAW("twirk.message.quickpoll.template.draw"),
    TWIRK_MESSAGE_QUICKPOLL_TEMPLATE_WIN("twirk.message.quickpoll.template.win"),

    TWIRK_MESSAGE_VOTE_HELP("twirk.message.vote.help"),
    TWIRK_MESSAGE_VOTE_ALREADYVOTED("twirk.message.vote.alreadyvoted"),
    TWIRK_MESSAGE_VOTE_ADDEDVOTE("twirk.message.vote.addedvote"),
    TWIRK_MESSAGE_VOTE_INVALIDVOTE("twirk.message.vote.invalidvote"),
    TWIRK_MESSAGE_VOTE_NOPOLLACTIVE("twirk.message.vote.nopollactive"),

    TWIRK_MESSAGE_CHANGEVOTE_HELP("twirk.message.changevote.help"),
    TWIRK_MESSAGE_CHANGEVOTE_ALREADYVOTED("twirk.message.changevote.alreadyvoted"),
    TWIRK_MESSAGE_CHANGEVOTE_ADDEDVOTE("twirk.message.changevote.addedvote"),
    TWIRK_MESSAGE_CHANGEVOTE_NOTYETVOTED("twirk.message.changevote.notyetvoted"),
    TWIRK_MESSAGE_CHANGEVOTE_INVALIDVOTE("twirk.message.changevote.invalidvote"),
    TWIRK_MESSAGE_CHANGEVOTE_NOPOLLACTIVE("twirk.message.changevote.nopollactive"),
    TWIRK_MESSAGE_CONVERSION_HELP("twirk.message.conversion.help"),
    TWIRK_MESSAGE_CONVERSION_EXAMPLE("twirk.message.conversion.example"),
    TWIRK_MESSAGE_CONVERSION_INVALIDARGUMENTAMOUNT("twirk.message.conversion.invalidargumentamount"),
    TWIRK_MESSAGE_CONVERSION_SAMETYPES("twirk.message.conversion.sametypes"),
    TWIRK_MESSAGE_CONVERSION_RESULT("twirk.message.conversion.result"),
    TWIRK_MESSAGE_CONVERSION_NOTANUMBER("twirk.message.conversion.notanumber"),
    TWIRK_MESSAGE_CONVERSION_GENERALERROR("twirk.message.conversion.generalerror"),
    TWIRK_MESSAGE_CONVERSION_OPTIONS("twirk.message.conversion.options"),
    SCENE_INVENTORY_MENU_TOOLS_CONVERSION("scene.inventory.menu.tools.conversion"),
    CONVERSIONS_TYPE_WEIGHT("conversions.type.weight"),
    CONVERSIONS_TYPE_VOLUME("conversions.type.volume"),
    CONVERSIONS_TYPE_LENGTH("conversions.type.length"),
    CONVERSIONS_TYPE_TEMPERATURE("conversions.type.temperature"),
    TWIRK_MESSAGE_QUESTION_CONFIRMATION("twirk.message.question.confirmation"),
    TWIRK_MESSAGE_QUESTION_HELP("twirk.message.question.help"),
    TWIRK_CONNECTION_NOTCONNECTED("twirk.connection.notconnected");
    private final String key;

    MessageKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
