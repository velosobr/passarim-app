// A tela SwiftUI que hospeda o app Compose.
import SwiftUI
import PassarimApp

/// Hospeda a UI em Compose Multiplatform. A configuração vem do xcconfig, via Info.plist.
struct ComposeView: UIViewControllerRepresentable {
    /// Cria o controlador do Compose com a configuração lida do Info.plist.
    func makeUIViewController(context: Context) -> UIViewController {
        let info = Bundle.main.infoDictionary
        let baseUrl = info?["BffBaseUrl"] as? String ?? ""
        let isDebug = (info?["PassarimDebug"] as? String) == "YES"
        // No debug, a mídia local (localhost) é reescrita para o mesmo host do BFF.
        let mediaHost = isDebug ? URL(string: baseUrl)?.host : nil
        let versionName = info?["CFBundleShortVersionString"] as? String ?? ""
        return MainViewControllerKt.MainViewController(
            baseUrl: baseUrl,
            isDebug: isDebug,
            mediaHostOverride: mediaHost,
            versionName: versionName
        )
    }

    /// Nada a atualizar: o Compose cuida da própria tela.
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

/// A tela principal: o Compose ocupa tudo, inclusive atrás das barras do sistema.
struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
