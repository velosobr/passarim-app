// Ponto de entrada do app iOS, em SwiftUI.
import SwiftUI

/// @main marca o início do app; a única janela mostra a ContentView.
@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea()
        }
    }
}
