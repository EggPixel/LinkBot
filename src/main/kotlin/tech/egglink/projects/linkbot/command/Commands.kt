package tech.egglink.projects.linkbot.command

import tech.egglink.projects.linkbot.command.cmd.CommandExit
import tech.egglink.projects.linkbot.command.cmd.CommandHelp
import tech.egglink.projects.linkbot.command.cmd.CommandLogin
import tech.egglink.projects.linkbot.command.cmd.CommandLogout

class Commands {
    private val commands = arrayListOf<CommandHandler>()

    /**
     * 注册一个命令
     * @param command 命令处理器
     * */
    private fun registerCommand(command: CommandHandler) {
        commands.add(command)
    }

    /**
     * 注册所有命令
     * */
    fun registerCommands() {
        registerCommand(CommandExit())
        registerCommand(CommandHelp())
        registerCommand(CommandLogin())
        registerCommand(CommandLogout())
    }

    /**
     * 获取所有命令
     * */
    fun getCommands(): List<CommandHandler> {
        return commands
    }

    /**
     * 运行一个命令
     * @param name 命令名
     * */
    suspend fun runCommand(name: String, sender: CommandSender, arg: Array<String>): CommandResult {
        var foundCommand: CommandHandler? = null
        for (command in commands) {
            if (command.entry.name == name) {
                foundCommand = command
            } else {
                for (alias in command.entry.aliases) {
                    if (alias == name) {
                        foundCommand = command
                    }
                }
            }
        }
        if (foundCommand != null) {
            // args若为空则使用默认参数
            val args = if (arg.isEmpty()) foundCommand.entry.defaultArgs else arg
            // 命令Type
            when (foundCommand.entry.type) {
                CommandType.Console -> {
                    if (sender is ConsoleCommandSender) {
                        if (sender.hasPermission(foundCommand.entry.permission)) { // 检查权限
                            // 检查参数
                            foundCommand.entry.argsType.let {
                                if (args.size != it.size) {
                                    return CommandResult.INVALID_USAGE
                                }
                                for (i in it.indices) {
                                    when (it[i]) {
                                        "String" -> {
                                            // 任何参数都可以
                                        }
                                        "Int" -> {
                                            if (!args[i].matches(Regex("[0-9]+"))) {
                                                return CommandResult.INVALID_USAGE
                                            }
                                        }
                                        "Double" -> {
                                            if (!args[i].matches(Regex("[0-9]+\\.[0-9]+"))) {
                                                return CommandResult.INVALID_USAGE
                                            }
                                        }
                                        "Boolean" -> {
                                            if (args[i] != "true" && args[i] != "false") {
                                                return CommandResult.INVALID_USAGE
                                            }
                                        }
                                        else -> {
                                            return CommandResult.ERROR
                                        }
                                    }
                                } // 参数类型检查
                                return foundCommand.execute(sender, args)
                            }
                        } else {
                            return CommandResult.NO_PERMISSION
                        }
                    } else {
                        return CommandResult.FAILURE
                    }
                }
                CommandType.Bot -> {
                    if (sender is BotCommandSender) {
                        if (sender.hasPermission(foundCommand.entry.permission)) { // 检查权限
                            // 检查参数
                            foundCommand.entry.argsType.let {
                                if (args.size != it.size) {
                                    return CommandResult.INVALID_USAGE
                                }
                                for (i in it.indices) {
                                    when (it[i]) {
                                        "String" -> {
                                            // 任何参数都可以
                                        }
                                        "Int" -> {
                                            if (!args[i].matches(Regex("[0-9]+"))) {
                                                return CommandResult.INVALID_USAGE
                                            }
                                        }
                                        "Double" -> {
                                            if (!args[i].matches(Regex("[0-9]+\\.[0-9]+"))) {
                                                return CommandResult.INVALID_USAGE
                                            }
                                        }
                                        "Boolean" -> {
                                            if (args[i] != "true" && args[i] != "false") {
                                                return CommandResult.INVALID_USAGE
                                            }
                                        }
                                        else -> {
                                            return CommandResult.ERROR
                                        }
                                    }
                                } // 参数类型检查结束
                                return foundCommand.execute(sender, args)
                            }
                        } else {
                            return CommandResult.NO_PERMISSION
                        }
                    } else {
                        return CommandResult.FAILURE
                    }
                }
            }
        }
        return CommandResult.NOT_FOUND
    }
}
