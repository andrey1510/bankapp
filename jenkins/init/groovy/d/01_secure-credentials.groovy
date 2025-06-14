import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import hudson.util.Secret
import jenkins.model.*
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl

def env = System.getenv()

def githubUsername = env['GITHUB_USERNAME']
def githubToken = env['GITHUB_TOKEN']
def ghcrToken = env['GHCR_TOKEN']
def dockerRegistry = env['DOCKER_REGISTRY']
def dockerHubUsername = env['DOCKER_HUB_USERNAME']
def dockerHubPassword = env['DOCKER_HUB_PASSWORD']

def store = Jenkins.instance.getExtensionList(
        'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
)[0].getStore()

if (githubUsername && githubToken) {
    println "--> Creating credential: github-creds (username + token)"
    def githubCreds = new UsernamePasswordCredentialsImpl(
            CredentialsScope.GLOBAL,
            "github-creds",
            "GitHub credentials from ENV",
            githubUsername,
            githubToken
    )
    store.addCredentials(Domain.global(), githubCreds)
}

if (githubUsername) {
    println "--> Creating credential: GITHUB_USERNAME (plain string)"
    def usernameCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "GITHUB_USERNAME",
            "GitHub username only (for GHCR login)",
            Secret.fromString(githubUsername)
    )
    store.addCredentials(Domain.global(), usernameCred)
}

if (ghcrToken) {
    println "--> Creating credential: GHCR_TOKEN"
    def ghcrCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "GHCR_TOKEN",
            "GHCR token from ENV",
            Secret.fromString(ghcrToken)
    )
    store.addCredentials(Domain.global(), ghcrCred)
}

if (dockerHubUsername) {
    println "--> Creating credential: docker hub creds"
    def dockerHubUsernameCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "DOCKER_HUB_USERNAME",
            "DOCKER_HUB_USERNAME token from ENV",
            Secret.fromString(dockerHubUsername)
    )

    store.addCredentials(Domain.global(), dockerHubUsernameCred)

    def dockerHubPasswordCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "DOCKER_HUB_PASSWORD",
            "DOCKER_HUB_PASSWORD token from ENV",
            Secret.fromString(dockerHubPassword)
    )
    store.addCredentials(Domain.global(), dockerHubPasswordCred)
}

if (dockerRegistry) {
    println "--> Creating credential: DOCKER_REGISTRY"
    def registryCred = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            "DOCKER_REGISTRY",
            "Docker registry address from ENV",
            Secret.fromString(dockerRegistry)
    )
    store.addCredentials(Domain.global(), registryCred)
}

println "--> Credential setup complete."