package com.carp2

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

fun main() {
    println("Hi!")
    val charactersData = getJSONFile("level1.json")
    println("File was getting successfully!")
    println(charactersData.toString())

    start_game(charactersData)
}

fun getJSONFile(fileName: String): GameData {
    val outputFile: GameData = Json.decodeFromString(File("src/main/res/databases/levels/level1/json/$fileName").readText())
    return outputFile
}

@Serializable
data class GameData(var characters: ArrayList<Character>){
    fun getCharacters():String {
        var outputString = ""
        for(character in characters){
            val name: String = character.name
            val health: Int = character.health
            val weapon: Weapon? = character.weapon

            outputString += "\nИмя: $name, Здоровье: $health, Оружие: $weapon"

            if (weapon!=null){
                val weapon_name: String = weapon.name
                val weapon_damage: Int = weapon.damage
                outputString += " (Название оружия: $weapon_name, Урон оружия: $weapon_damage); "
            }
            else{
                outputString += "; "
            }
        }
        return outputString
    }

    fun addCharacter(character: Character): Boolean{
        try {
            characters.add(character)
            return true
        } catch (e:IOException){
            println("Something went wrong...")
        }
        return false
    }
}

@Serializable
data class Character(
    val name : String,
    var health : Int,
    val weapon : Weapon?,
    val heal_amount : Int,
    var armor : Int
) {
    val currentDamage: Int get() = weapon?.damage ?: 0
    fun attack(enemy: Character){
        if (currentDamage <= 0) {
            println("${name} не имеет оружия и не может атаковать.")
            return
        }
        if(enemy.health==0){
            println("Вы не можете атаковать персонажа ${enemy.name}, он уже убит.")
            return
        }
        else if(enemy.health>currentDamage && enemy.armor>currentDamage){
            enemy.armor -= currentDamage
            println("Персонаж ${name} нанёс персонажу ${enemy.name} урон: ${currentDamage}. " +
                    "Оставшееся количество hp у ${enemy.name}: ${enemy.health}, оставшееся количество брони: ${enemy.armor}.")
            return
        }
        else if(enemy.health>currentDamage && enemy.armor<=currentDamage){
            enemy.armor = 0
            enemy.health -= currentDamage-enemy.armor
            println("Персонаж ${name} нанёс персонажу ${enemy.name} урон: ${currentDamage}. " +
                    "У ${enemy.name} не осталось брони, оставшееся количество hp: ${enemy.health}")
            return
        }
        else if(enemy.health<=currentDamage){
            enemy.health = 0
            println("Персонаж ${enemy.name} был убит персонажем ${name}.")
            return
        }
        else{
            throw Exception("Произошла ошибка (значение hp у ${enemy.name} < 0 при попытке атаковать его). Перезапустите код.")
        }
    }
    fun heal(){
        if(health==0){
            println("Невозможно захилить погибшего персонажа ${name}.")
            return
        }
        else if(health>0){
            health+=heal_amount
            println("Персонаж ${name} захилился. Количество hp: ${health}.")
            return
        }
        else{
            throw Exception("Произошла ошибка (значение hp у ${name} < 0 при его попытке захилиться). Перезапустите код.")
        }
    }

}

@Serializable
data class Weapon(
    val name: String,
    val damage: Int
)

//fun init_characters(): ArrayList<ArrayList<Character>>{
//    val elf : Character = Character("Эльф", 100, 15, 5, 30)
//    val ork : Character = Character("Орк", 150, 10, 0, 50)
//    val person : Character = Character("Человек", 50, 27, 10, 50)
//    val characters : ArrayList<Character> = arrayListOf(elf, ork, person)
//    val enemies : ArrayList<Character> = arrayListOf(elf, ork)
//    val listToReturn : ArrayList<ArrayList<Character>> = arrayListOf(characters, enemies)
//
//    return listToReturn
//}

fun start_game(gameData: GameData){
    println("Игра начинается...")
    println("   \n".repeat(10))
    println("Список персонажей: ")
    var characters = gameData.getCharacters()
    println(characters)
    print("Хотите играть? (да/нет): ")
    var answer = readln().lowercase()
    while (answer!="да"){
        println("Ответ нераспознан. Введите верный ответ (да): ")
        answer = readln().lowercase()
    }
    val person: Character = Character("Person", 10, Weapon("Палка", 5), 10, 5)
    gameData.addCharacter(person)
    println("Добавлен персонаж: $person")
    fight_logic(gameData, person)
}

fun fight_logic(gameData: GameData, person: Character) {
    gameData.characters.removeLast()
    var enemies = gameData.characters

    if (enemies.isEmpty()) {
        println("Нет доступных противников для боя.")
        return
    }

    var defeatedCount = 0

    while (person.health > 0) {
        val enemy = enemies.random()

        println("\n=== Новый противник: ${enemy.name} ===")
        println("У ${enemy.name}: HP = ${enemy.health}, броня = ${enemy.armor}, " +
                "оружие = ${enemy.weapon?.name ?: "нет"} (${enemy.currentDamage} урона)")
        println("У ${person.name}: HP = ${person.health}, броня = ${person.armor}")

        while (person.health > 0 && enemy.health > 0) {
            println("\n--- Ваш ход ---")
            println("1. Атака")
            println("2. Хил")
            print("Выберите действие: ")

            val choice = try {
                readln().toInt()
            } catch (e: Exception) {
                println("Непонятно, введите ещё раз:")
                readln().toInt()
                continue
            }

            when (choice) {
                1 -> person.attack(enemy)
                2 -> person.heal()
                else -> {
                    println("Неизвестное действие. Попробуйте снова.")
                    continue
                }
            }

            if (enemy.health <= 0) {
                gameData.characters.remove(enemy)
                enemies = gameData.characters
            }

            println("\n--- Ход ${enemy.name} ---")
            val enemyAction = decideEnemyAction(enemy)
            when (enemyAction) {
                "attack" -> enemy.attack(person)
                "heal" -> enemy.heal()
            }
        }

        if (person.health <= 0) {
            println("\nТы был побеждён. Но ты не проиграл, потому что ты можешь запустить игру заново. " +
                    "Но кто ты после того, что убил стольких жителей магического леса — герой или злодей?..")
            return
        }

        defeatedCount++
        println("\n${enemy.name} побеждён! Побеждено противников: $defeatedCount")
    }
}

enum class EnemyAction { ATTACK, HEAL }
fun decideEnemyAction(enemy: Character): String {
    val lowHP = 10
    if (enemy.health <= lowHP && enemy.heal_amount > 0 && Math.random() < 0.5) {
        return "heal"
    }
    return "attack"
}