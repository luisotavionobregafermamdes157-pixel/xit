# PMT FF PRO

Aplicação Android nativa em Kotlin para ferramentas gaming legítimas.

## Funcionalidades
- Interface escura com tema vermelho/azul.
- Sensibilidade ajustável e perfis locais.
- Treino independente de precisão com alvos e temporizador.
- Diagnóstico básico de dispositivo e RAM disponível.
- Deteção heurística simples de possíveis ficheiros root.
- Workflow GitHub Actions para compilar e publicar o APK como artefacto.

## Compilar APK
O workflow `.github/workflows/build-apk.yml` compila um APK de debug em cada push para `main` e também pode ser executado manualmente em **Actions → Build PMT FF PRO APK → Run workflow**. O APK fica disponível em **Artifacts** durante 14 dias após uma compilação bem-sucedida.

Também podes abrir este repositório no Android Studio, instalar Android SDK Platform 35 e executar **Build → Build APK(s)**.

## Limitações e segurança
- Não injeta código nem modifica a memória/ficheiros do Free Fire.
- Não inclui aimbot, wallhack ou bypass anti-cheat.
- Os valores de sensibilidade são sugestões para aplicar manualmente.
- A deteção de root é heurística e pode falhar.
- O Android não oferece uma API pública universal para ler o FPS interno de outro jogo.
- Esta versão não tem painel flutuante sobre outras aplicações nem comandos administrativos root.

## Compatibilidade
- minSdk: Android 8.0 (API 26)
- targetSdk/compileSdk: 35
- Permissões: não solicita permissões perigosas.
