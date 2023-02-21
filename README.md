# KaKaoBookSearch

# Hilt 사용시 Error 사항
The following options were not recognized by any processor: '[dagger.fastInit, kapt.kotlin.generated]'
다음과 같은 에러가 뜬다면,

1번.

https://stackoverflow.com/questions/70550883/warning-the-following-options-were-not-recognized-by-any-processor-dagger-f
Move the id "kotlin-kapt" to the bottom of plugins{} in build.gradle Module-level. 수행해주면 됨.

해도 안되면


2번.

https://stackoverflow.com/questions/58216710/the-following-options-were-not-recognized-by-any-processor-kapt-kotlin-genera

kapt "android.arch.persistence.room:compiler:$room_version"를 적용 sync
