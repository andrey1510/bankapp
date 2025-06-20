import jenkins.model.*
import org.jenkinsci.plugins.github_branch_source.*
import jenkins.branch.*
import org.jenkinsci.plugins.workflow.multibranch.*

def env = System.getenv()
def instance = Jenkins.get()

def jobName       = "bankapp"
def githubRepo    = env['GITHUB_REPOSITORY']
def credentialsId = "github-creds"
def scriptPath    = "jenkins/Jenkinsfile"

println "--> Starting create-multibranch-job.groovy"

if (!githubRepo) {
    println "Env variable GITHUB_REPOSITORY not set (example: owner/repo)"
    return
}

println "--> GITHUB_REPOSITORY = ${githubRepo}"

if (instance.getItem(jobName) != null) {
    println "--> Multibranch job '${jobName}' already exists. Skipping."
    return
}

def parts = githubRepo.split('/')
if (parts.length != 2) {
    println "Incorrect format GITHUB_REPOSITORY. Expected: owner/repo"
    return
}
def owner = parts[0]
def repo  = parts[1]

def source = new GitHubSCMSource(owner, repo)
source.setCredentialsId(credentialsId)
source.setTraits([
        new BranchDiscoveryTrait(1),
        new OriginPullRequestDiscoveryTrait(1),
        new ForkPullRequestDiscoveryTrait(1, new ForkPullRequestDiscoveryTrait.TrustPermission())
])

def branchSource = new BranchSource(source)
branchSource.setStrategy(new DefaultBranchPropertyStrategy([] as BranchProperty[]))

def mbp = new WorkflowMultiBranchProject(instance, jobName)
mbp.getSourcesList().add(branchSource)

def factory = new WorkflowBranchProjectFactory()
factory.setScriptPath(scriptPath)
mbp.setProjectFactory(factory)

instance.add(mbp, jobName)
mbp.save()
mbp.scheduleBuild2(0)

println "--> Multibranch job '${jobName}' created and started on '${githubRepo}'"