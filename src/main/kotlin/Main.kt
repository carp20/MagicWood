package com.carp2

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.util.Scanner

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
        if(enemy.health==0){
            println("Вы не можете атаковать персонажа ${enemy.name}, он уже убит.")
        }
        else if(enemy.health>currentDamage && enemy.armor>currentDamage){
            enemy.armor -= currentDamage
            println("Персонаж ${name} нанёс персонажу ${enemy.name} урон: ${currentDamage}. " +
                    "Оставшееся количество hp у ${ enemy.name}: ${enemy.health}, оставшееся количество брони: ${enemy.armor}.")
        }
        else if(enemy.health>currentDamage && enemy.armor<currentDamage){
            enemy.armor = 0
            enemy.health -= currentDamage-armor
            println("Персонаж ${name} нанёс персонажу ${enemy.name} урон: ${currentDamage}. " +
                    "У ${enemy.name} не осталось брони, оставшееся количество hp: ${enemy.health}")
        }
        else if(enemy.health<=currentDamage){
            enemy.health = 0
            println("Персонаж ${enemy.name} был убит персонажем ${name}.")
        }
        else{
            throw Exception("Произошла ошибка (значение hp у ${enemy.name} < 0). Перезапустите код.")
        }
    }
    fun heal(){
        if(health==0){
            println("Невозможно захилить погибшего персонажа ${name}.")
        }
        else if(health>0){
            health+=heal_amount
            println("Персонаж ${name} захилился. Количество hp: ${health}.")
        }
        else{
            throw Exception("Произошла ошибка (значение hp у ${name} < 0). Перезапустите код.")
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
}