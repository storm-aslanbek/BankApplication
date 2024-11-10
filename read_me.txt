Всем привет! Данный проект разработан для регианального хакатона ITII Hackathon III. Суть проекта в мобильном 
супер приложении банка для андройда. Приложение на казахском языке.

Приложение разработан на языке котлин. Сервер работает на python Fast API. База данных MongoDB.

Функционал 1.0 версии: регистрация и авторизация, мой банк, мобильные переводы, поддержка пользователей на основе моделя gpt-3.5-turbo.
Далее есть страницы в которых нету функционала и которые являются макетами: сообщения, часто задаваемые вопросы.



-----------------------------------------------------------------------------------------------------------------------------------------



Ниже приведены важные файлы и структура проекта

в папке bank_server в корневом проекте хранится файл сервера python fast api

Активности страниц идут по адресу
D:\AndroidStudioProjects\MyApplication2\app\src\main\java\com\example\myapplication\activities

Дизайн активностей по странице
D:\AndroidStudioProjects\MyApplication2\app\src\main\res\layout

Рядом директория retrofit для связи с сервером
D:\AndroidStudioProjects\MyApplication2\app\src\main\java\com\example\myapplication\retrofit

Рядом класс котлин для связи с сервером где хранится url сервера. Экспортируется в активности
D:\AndroidStudioProjects\MyApplication2\app\src\main\java\com\example\myapplication\RetrofitClient.kt

Изоброжения для приложения хранятся по адресу
D:\AndroidStudioProjects\MyApplication2\app\src\main\res\drawable

Файл для корректной работы сервера по локальному ip
D:\AndroidStudioProjects\MyApplication2\app\src\main\res\xml\network_security_config.xml

Файл всех привязок проекта. Привязки активностей и настроек локальной сети
D:\AndroidStudioProjects\MyApplication2\app\manifests\AndroidManifests.xml