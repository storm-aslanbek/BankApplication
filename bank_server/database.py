# База данных
from pymongo import MongoClient
from bson.objectid import ObjectId

# Подключение к базам данных
client = MongoClient("mongodb://localhost:27017/")
db = client["bank"]
collection = db["users"]

# Добавление пользователя
def insert_item(user_data):
    try:
        return collection.insert_one(user_data).inserted_id
    except Exception as e:
        print(f"Error inserting item: {e}")
        return None


# Поиск пользователя
def find_item(phone_number):
    return collection.find_one({"phone_number": phone_number})
