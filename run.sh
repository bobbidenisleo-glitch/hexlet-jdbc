#!/bin/bash

echo "Выберите версию для запуска:"
echo "1) Основная версия (Application)"
echo "2) Улучшенная версия (ApplicationEnhanced)"
echo "3) Сборка проекта"
echo "4) Очистка проекта"
read -p "Ваш выбор (1-4): " choice

case $choice in
    1)
        ./gradlew run
        ;;
    2)
        ./gradlew runEnhanced
        ;;
    3)
        ./gradlew build
        ;;
    4)
        ./gradlew clean
        ;;
    *)
        echo "Неверный выбор"
        ;;
esac
