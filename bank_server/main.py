# Сервер ФАСТ АПИ
import openai
from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel

import config
import database
from database import insert_item, find_item
from bson.objectid import ObjectId

openai.api_key = config.GPT_API_KEY
app = FastAPI()


# Форма запросов из приложения
class Item(BaseModel):
    last_name: str
    first_name: str
    phone_number: str
    password: str
    balance: int

class AuthData(BaseModel):
    phone_number: str
    password: str

class UserSearchData(BaseModel):
    phone_number: str

class TransferData(BaseModel):
    phone_number: str
    balance: int
    sender_phone: str

class ChatRequest(BaseModel):
    message: str



# Отслежование и обработка запросов из приложения
@app.get("/")
def read_root():
    return {"Hello": "World"}

# Регистрация
@app.post("/register/")
async def create_item(phone: Item):
    phone_number = insert_item(phone.dict())
    return {"id": str(phone_number)}

# Вход
@app.post("/auth/")
async def login(login_data: AuthData):
    user = database.collection.find_one({"phone_number": login_data.phone_number})

    if user and user["password"] == login_data.password:
        return {"last_name": f"{user['last_name']}", "first_name": f"{user['first_name']}", "phone_number": f"{user['phone_number']}",
                "password": f"{user['password']}", "balance": f"{user['balance']}"}
    else:
        raise HTTPException(status_code=401, detail="Invalid phone number or password")

# Поиск пользователя (Переводы)
@app.post("/user_search/")
async def user_search(user_search_data: UserSearchData):
    user = database.collection.find_one({"phone_number": user_search_data.phone_number})

    if user:
        return {"last_name": f"{user['last_name']}", "first_name": f"{user['first_name']}", "phone_number": f"{user['phone_number']}",
                "password": f"{user['password']}", "balance": f"{user['balance']}"}
    else:
        raise HTTPException(status_code=401, detail="Invalid phone number or password")

# Переводы
@app.post("/transfer/")
async def transfer(transfer_data: TransferData):
    sender = database.collection.find_one({"phone_number": transfer_data.sender_phone})
    if not sender:
        raise HTTPException(status_code=404, detail="Sender not found")

    receiver = database.collection.find_one({"phone_number": transfer_data.phone_number})
    if not receiver:
        raise HTTPException(status_code=404, detail="Receiver not found")

    if sender['balance'] < transfer_data.balance:
        raise HTTPException(status_code=400, detail="Insufficient funds")

    # Делаем перевод
    database.collection.update_one({"phone_number": transfer_data.sender_phone}, {"$inc": {"balance": - transfer_data.balance}})
    database.collection.update_one({"phone_number": transfer_data.phone_number}, {"$inc": {"balance": transfer_data.balance}})

    return {"message": "Transfer successful", "new_balance": sender['balance'] - transfer_data.balance}


# Поддержка GPT
@app.post("/support/")
async def chat_with_gpt(request: ChatRequest):
    try:
        # Формируем сообщения для ChatGPT
        messages = [
            {"role": "system", "content":  '''
                Сен мобильді банк қосымшасының қолдау қызметінің көмекшісісің. 
                Қолданушыларға қосымша функциялары бойынша көмек көрсету керек:
                1. Қолданушының тіркелуі мен кіруі. Аты-жөні, телефон номері, құпиясөз арқылы жүзеге асады
                2. 'Менің банкым' бөлімінде есепшоттағы ақшаны көру.
                3. 'Аударымдар' бөлімінде басқа қолданушыға ақша аудару. Қабылдаушы телефон нөмері арқылы
                4. Қолдау бөлімі арқылы қолданушыларға көмек көрсету (ChatGPT).
                5. Жиі қойылатын сұрақтарға жауап беру.
                6. Хабарламалармен жұмыс жасау.
                7. Басты бет функциялары бойынша көмек көрсету.
            
                Тапсырмаң - қолданушының сұрақтарына жауап беру және оларға жоғарыдағы функциялар бойынша көмек көрсету.
                Қысқаша әрі нақты жауап беруге тырыс
                '''},
            {"role": "user", "content": request.message}
        ]

        # Отправляем запрос к ChatGPT
        response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",  # Используем модель GPT-3.5-turbo
            messages=messages,
            max_tokens=250
        )

        # Возвращаем ответ
        return {"response": response['choices'][0]['message']['content'].strip()}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

