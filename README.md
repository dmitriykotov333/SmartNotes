# Тестовое задание “Умные заметки (Smart Notes)”
H1 Цель
Хранение коротких текстовых заметок и связанной информации.
Описание
Приложение должно запускаться на устройствах начиная с Android 4.1, быть
ориентированным на пользователя, иметь понятный и простой интерфейс на
стандартных элементах управления, работать без подвисаний и крешей.
Предметная область
Заметка состоит из двух текстовых полей: заголовок заметки и тело заметки. В
задачах разных уровней к заметке добавляются другие данные.
Задачи


| Уровень        | Задача           |
| -------------- |:----------------:|
| Уровень 0      | Экран списка заметок. Здесь отображается список ранее созданных заметок. Элемент
списка заголовок
заметки. В заголовке экрана кнопка
создания новой заметки. По
нажатию на элемент списка переход
к просмотру заметки. По нажатию на кнопку
создания создание
заметки. Так же здесь должна быть возможность удаления и
редактирования заметки.
Экран просмотра заметки. Здесь отображается содержимое заметки. Есть
возможность перейти к редактированию или удалить заметку.
Экран редактирования заметки. Аналогичен экрану просмотра, но с возможностью
редактирования заголовка и тела заметки. Есть возможность сохранить изменения,
отменить изменения и удалить заметку. | 
| col 2 is      | centered      |  
| zebra stripes | are neat      |  



Уровень 1
Возможность экспорта заметки как текстового фала
Уровень 2
Заметки храниться в базе SQLite.
Уровень 3
Важность заметок. Каждой заметке можно присвоить важность зелёная
(низкая),
жёлтая (средняя), красная (важная), без цвета (важность не указана). В списке заметки
должны подсвечиваться соответствующим цветом.
Уровень 4
К заметке можно прикрепить фото. Превью фото видно в списке заметок. Есть
возможность просомтра полноразмерного фото.
Уровень 5
С каждой заметкой ассоциированы GPSкоординаты.
По умолчанию ставятся текущие
(если доступны). Есть возможность указания координат вручную с карты. Есть
возможность просмотра заметок на карте в виде пинов, переход с карты на конкретную
заметку.
Уровень 6
Шаринг заметок в социальные сети: ВК, Facebook, Twitter и тп. Вместе с текстом
заметки должны шариться и остальные данные gps
кооринаты, фото и тп.


