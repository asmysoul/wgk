




<!DOCTYPE html>
<html class="   ">
  <head prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb# object: http://ogp.me/ns/object# article: http://ogp.me/ns/article# profile: http://ogp.me/ns/profile#">
    <meta charset='utf-8'>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    
    
    <title>js-xss/dist/xss.js at v0.1.9 · leizongmin/js-xss</title>
    <link rel="search" type="application/opensearchdescription+xml" href="/opensearch.xml" title="GitHub" />
    <link rel="fluid-icon" href="https://github.com/fluidicon.png" title="GitHub" />
    <link rel="apple-touch-icon" sizes="57x57" href="/apple-touch-icon-114.png" />
    <link rel="apple-touch-icon" sizes="114x114" href="/apple-touch-icon-114.png" />
    <link rel="apple-touch-icon" sizes="72x72" href="/apple-touch-icon-144.png" />
    <link rel="apple-touch-icon" sizes="144x144" href="/apple-touch-icon-144.png" />
    <meta property="fb:app_id" content="1401488693436528"/>

      <meta content="@github" name="twitter:site" /><meta content="summary" name="twitter:card" /><meta content="leizongmin/js-xss" name="twitter:title" /><meta content="js-xss - Sanitize untrusted HTML (to prevent XSS) with a configuration specified by a Whitelist. 根据白名单过滤HTML(防止XSS攻击)" name="twitter:description" /><meta content="https://avatars3.githubusercontent.com/u/841625?s=400" name="twitter:image:src" />
<meta content="GitHub" property="og:site_name" /><meta content="object" property="og:type" /><meta content="https://avatars3.githubusercontent.com/u/841625?s=400" property="og:image" /><meta content="leizongmin/js-xss" property="og:title" /><meta content="https://github.com/leizongmin/js-xss" property="og:url" /><meta content="js-xss - Sanitize untrusted HTML (to prevent XSS) with a configuration specified by a Whitelist. 根据白名单过滤HTML(防止XSS攻击)" property="og:description" />

    <link rel="assets" href="https://assets-cdn.github.com/">
    <link rel="conduit-xhr" href="https://ghconduit.com:25035">
    <link rel="xhr-socket" href="/_sockets" />

    <meta name="msapplication-TileImage" content="/windows-tile.png" />
    <meta name="msapplication-TileColor" content="#ffffff" />
    <meta name="selected-link" value="repo_source" data-pjax-transient />
      <meta name="google-analytics" content="UA-3769691-2">

    <meta content="collector.githubapp.com" name="octolytics-host" /><meta content="collector-cdn.github.com" name="octolytics-script-host" /><meta content="github" name="octolytics-app-id" /><meta content="DA6DE0BA:2EB6:1ED6739:53C9E02A" name="octolytics-dimension-request_id" /><meta content="4941967" name="octolytics-actor-id" /><meta content="youblade" name="octolytics-actor-login" /><meta content="6ff6a9feba45c20066e17d8d02b6c68fc3329ef70f36175a282e779e08a31c1a" name="octolytics-actor-hash" />
    

    
    
    <link rel="icon" type="image/x-icon" href="https://assets-cdn.github.com/favicon.ico" />


    <meta content="authenticity_token" name="csrf-param" />
<meta content="sxBjlkkvNKf1TGq/xdvyAQ7pWFNh/hCarnIw1pybClB85REy1gSBQ0qlBwQnKlQRmBRkymMM9SLgAUvN8txRkw==" name="csrf-token" />

    <link href="https://assets-cdn.github.com/assets/github-08cc4d21dbfdd953c85afbf75762f5b6e145620d.css" media="all" rel="stylesheet" type="text/css" />
    <link href="https://assets-cdn.github.com/assets/github2-7fa45bc1a10ba6acbc768b848364578bfb855eac.css" media="all" rel="stylesheet" type="text/css" />
    


    <meta http-equiv="x-pjax-version" content="db52f4e25078b55191f8b1f2d9a3a573">

      
  <meta name="description" content="js-xss - Sanitize untrusted HTML (to prevent XSS) with a configuration specified by a Whitelist. 根据白名单过滤HTML(防止XSS攻击)" />


  <meta content="841625" name="octolytics-dimension-user_id" /><meta content="leizongmin" name="octolytics-dimension-user_login" /><meta content="5857265" name="octolytics-dimension-repository_id" /><meta content="leizongmin/js-xss" name="octolytics-dimension-repository_nwo" /><meta content="true" name="octolytics-dimension-repository_public" /><meta content="false" name="octolytics-dimension-repository_is_fork" /><meta content="5857265" name="octolytics-dimension-repository_network_root_id" /><meta content="leizongmin/js-xss" name="octolytics-dimension-repository_network_root_nwo" />

  <link href="https://github.com/leizongmin/js-xss/commits/v0.1.9.atom" rel="alternate" title="Recent Commits to js-xss:v0.1.9" type="application/atom+xml" />

  </head>


  <body class="logged_in  env-production windows vis-public page-blob">
    <a href="#start-of-content" tabindex="1" class="accessibility-aid js-skip-to-content">Skip to content</a>
    <div class="wrapper">
      
      
      
      


      <div class="header header-logged-in true">
  <div class="container clearfix">

    <a class="header-logo-invertocat" href="https://github.com/" aria-label="Homepage">
  <span class="mega-octicon octicon-mark-github"></span>
</a>


    
    <a href="/notifications" aria-label="You have no unread notifications" class="notification-indicator tooltipped tooltipped-s" data-hotkey="g n">
        <span class="mail-status all-read"></span>
</a>

      <div class="command-bar js-command-bar  in-repository">
          <form accept-charset="UTF-8" action="/search" class="command-bar-form" id="top_search_form" method="get">

<div class="commandbar">
  <span class="message"></span>
  <input type="text" data-hotkey="s, /" name="q" id="js-command-bar-field" placeholder="Search or type a command" tabindex="1" autocapitalize="off"
    
    data-username="youblade"
    data-repo="leizongmin/js-xss"
  >
  <div class="display hidden"></div>
</div>

    <input type="hidden" name="nwo" value="leizongmin/js-xss" />

    <div class="select-menu js-menu-container js-select-menu search-context-select-menu">
      <span class="minibutton select-menu-button js-menu-target" role="button" aria-haspopup="true">
        <span class="js-select-button">This repository</span>
      </span>

      <div class="select-menu-modal-holder js-menu-content js-navigation-container" aria-hidden="true">
        <div class="select-menu-modal">

          <div class="select-menu-item js-navigation-item js-this-repository-navigation-item selected">
            <span class="select-menu-item-icon octicon octicon-check"></span>
            <input type="radio" class="js-search-this-repository" name="search_target" value="repository" checked="checked" />
            <div class="select-menu-item-text js-select-button-text">This repository</div>
          </div> <!-- /.select-menu-item -->

          <div class="select-menu-item js-navigation-item js-all-repositories-navigation-item">
            <span class="select-menu-item-icon octicon octicon-check"></span>
            <input type="radio" name="search_target" value="global" />
            <div class="select-menu-item-text js-select-button-text">All repositories</div>
          </div> <!-- /.select-menu-item -->

        </div>
      </div>
    </div>

  <span class="help tooltipped tooltipped-s" aria-label="Show command bar help">
    <span class="octicon octicon-question"></span>
  </span>


  <input type="hidden" name="ref" value="cmdform">

</form>
        <ul class="top-nav">
          <li class="explore"><a href="/explore">Explore</a></li>
            <li><a href="https://gist.github.com">Gist</a></li>
            <li><a href="/blog">Blog</a></li>
          <li><a href="https://help.github.com">Help</a></li>
        </ul>
      </div>

    

<ul id="user-links">
  <li>
    <a href="/youblade" class="name">
      <img alt="youblade" class=" js-avatar" data-user="4941967" height="20" src="https://avatars3.githubusercontent.com/u/4941967?s=140" width="20" /> youblade
    </a>
  </li>

  <li class="new-menu dropdown-toggle js-menu-container">
    <a href="#" class="js-menu-target tooltipped tooltipped-s" aria-label="Create new...">
      <span class="octicon octicon-plus"></span>
      <span class="dropdown-arrow"></span>
    </a>

    <div class="new-menu-content js-menu-content">
    </div>
  </li>

  <li>
    <a href="/settings/profile" id="account_settings"
      class="tooltipped tooltipped-s"
      aria-label="Account settings ">
      <span class="octicon octicon-tools"></span>
    </a>
  </li>
  <li>
    <form accept-charset="UTF-8" action="/logout" class="logout-form" method="post"><div style="margin:0;padding:0;display:inline"><input name="authenticity_token" type="hidden" value="9Vt5rYTa/E2kOuAHhsTQkz5Big94lOMrGEoQwq7LNBSHu7+4/5ddTe8uqkvdQJ605zlsR7v7csZOF50sCFz6zg==" /></div>
      <button class="sign-out-button tooltipped tooltipped-s" aria-label="Sign out">
        <span class="octicon octicon-sign-out"></span>
      </button>
</form>  </li>

</ul>

<div class="js-new-dropdown-contents hidden">
  

<ul class="dropdown-menu">
  <li>
    <a href="/new"><span class="octicon octicon-repo"></span> New repository</a>
  </li>
  <li>
    <a href="/organizations/new"><span class="octicon octicon-organization"></span> New organization</a>
  </li>


    <li class="section-title">
      <span title="leizongmin/js-xss">This repository</span>
    </li>
      <li>
        <a href="/leizongmin/js-xss/issues/new"><span class="octicon octicon-issue-opened"></span> New issue</a>
      </li>
</ul>

</div>


    
  </div>
</div>

      

        



      <div id="start-of-content" class="accessibility-aid"></div>
          <div class="site" itemscope itemtype="http://schema.org/WebPage">
    <div id="js-flash-container">
      
    </div>
    <div class="pagehead repohead instapaper_ignore readability-menu">
      <div class="container">
        
<ul class="pagehead-actions">

    <li class="subscription">
      <form accept-charset="UTF-8" action="/notifications/subscribe" class="js-social-container" data-autosubmit="true" data-remote="true" method="post"><div style="margin:0;padding:0;display:inline"><input name="authenticity_token" type="hidden" value="aKX4HG1OhVsUaJrPQgLdzvyjGVHwdSAT9qsryL51ecOFLD/ugbZi6tXJOYxBQGBDj1VE+1yGdp5GRVpC/XxdNg==" /></div>  <input id="repository_id" name="repository_id" type="hidden" value="5857265" />

    <div class="select-menu js-menu-container js-select-menu">
      <a class="social-count js-social-count" href="/leizongmin/js-xss/watchers">
        24
      </a>
      <a href="/leizongmin/js-xss/subscription"
        class="minibutton select-menu-button with-count js-menu-target" role="button" tabindex="0" aria-haspopup="true">
        <span class="js-select-button">
          <span class="octicon octicon-eye"></span>
          Watch
        </span>
      </a>

      <div class="select-menu-modal-holder">
        <div class="select-menu-modal subscription-menu-modal js-menu-content" aria-hidden="true">
          <div class="select-menu-header">
            <span class="select-menu-title">Notifications</span>
            <span class="octicon octicon-x js-menu-close" role="button" aria-label="Close"></span>
          </div> <!-- /.select-menu-header -->

          <div class="select-menu-list js-navigation-container" role="menu">

            <div class="select-menu-item js-navigation-item selected" role="menuitem" tabindex="0">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <div class="select-menu-item-text">
                <input checked="checked" id="do_included" name="do" type="radio" value="included" />
                <h4>Not watching</h4>
                <span class="description">Be notified when participating or @mentioned.</span>
                <span class="js-select-button-text hidden-select-button-text">
                  <span class="octicon octicon-eye"></span>
                  Watch
                </span>
              </div>
            </div> <!-- /.select-menu-item -->

            <div class="select-menu-item js-navigation-item " role="menuitem" tabindex="0">
              <span class="select-menu-item-icon octicon octicon octicon-check"></span>
              <div class="select-menu-item-text">
                <input id="do_subscribed" name="do" type="radio" value="subscribed" />
                <h4>Watching</h4>
                <span class="description">Be notified of all conversations.</span>
                <span class="js-select-button-text hidden-select-button-text">
                  <span class="octicon octicon-eye"></span>
                  Unwatch
                </span>
              </div>
            </div> <!-- /.select-menu-item -->

            <div class="select-menu-item js-navigation-item " role="menuitem" tabindex="0">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <div class="select-menu-item-text">
                <input id="do_ignore" name="do" type="radio" value="ignore" />
                <h4>Ignoring</h4>
                <span class="description">Never be notified.</span>
                <span class="js-select-button-text hidden-select-button-text">
                  <span class="octicon octicon-mute"></span>
                  Stop ignoring
                </span>
              </div>
            </div> <!-- /.select-menu-item -->

          </div> <!-- /.select-menu-list -->

        </div> <!-- /.select-menu-modal -->
      </div> <!-- /.select-menu-modal-holder -->
    </div> <!-- /.select-menu -->

</form>
    </li>

  <li>
    

  <div class="js-toggler-container js-social-container starring-container on">

    <form accept-charset="UTF-8" action="/leizongmin/js-xss/unstar" class="js-toggler-form starred" data-remote="true" method="post"><div style="margin:0;padding:0;display:inline"><input name="authenticity_token" type="hidden" value="IjkydEfDOd6JOWV8dVJ69Okc7vEIpeBfRLsa09RRpM71sL4HfyeutBh+R8v4B/Ed0S9QvVDX16DIgusDGdqQbA==" /></div>
      <button
        class="minibutton with-count js-toggler-target star-button"
        aria-label="Unstar this repository" title="Unstar leizongmin/js-xss">
        <span class="octicon octicon-star"></span>
        Unstar
      </button>
        <a class="social-count js-social-count" href="/leizongmin/js-xss/stargazers">
          201
        </a>
</form>
    <form accept-charset="UTF-8" action="/leizongmin/js-xss/star" class="js-toggler-form unstarred" data-remote="true" method="post"><div style="margin:0;padding:0;display:inline"><input name="authenticity_token" type="hidden" value="J3BFxuJchXdOyxJ0GJQjAPvGs6FWGCLxdy6O/F+K6fxnLomuz0rZvnJ+53+d6P5e213VzWqUVDBl4Cs+ppqxTw==" /></div>
      <button
        class="minibutton with-count js-toggler-target star-button"
        aria-label="Star this repository" title="Star leizongmin/js-xss">
        <span class="octicon octicon-star"></span>
        Star
      </button>
        <a class="social-count js-social-count" href="/leizongmin/js-xss/stargazers">
          201
        </a>
</form>  </div>

  </li>


        <li>
          <a href="/leizongmin/js-xss/fork" class="minibutton with-count js-toggler-target fork-button lighter tooltipped-n" title="Fork your own copy of leizongmin/js-xss to your account" aria-label="Fork your own copy of leizongmin/js-xss to your account" rel="nofollow" data-method="post">
            <span class="octicon octicon-repo-forked"></span>
            Fork
          </a>
          <a href="/leizongmin/js-xss/network" class="social-count">46</a>
        </li>

</ul>

        <h1 itemscope itemtype="http://data-vocabulary.org/Breadcrumb" class="entry-title public">
          <span class="repo-label"><span>public</span></span>
          <span class="mega-octicon octicon-repo"></span>
          <span class="author"><a href="/leizongmin" class="url fn" itemprop="url" rel="author"><span itemprop="title">leizongmin</span></a></span><!--
       --><span class="path-divider">/</span><!--
       --><strong><a href="/leizongmin/js-xss" class="js-current-repository js-repo-home-link">js-xss</a></strong>

          <span class="page-context-loader">
            <img alt="" height="16" src="https://assets-cdn.github.com/images/spinners/octocat-spinner-32.gif" width="16" />
          </span>

        </h1>
      </div><!-- /.container -->
    </div><!-- /.repohead -->

    <div class="container">
      <div class="repository-with-sidebar repo-container new-discussion-timeline js-new-discussion-timeline  ">
        <div class="repository-sidebar clearfix">
            

<div class="sunken-menu vertical-right repo-nav js-repo-nav js-repository-container-pjax js-octicon-loaders">
  <div class="sunken-menu-contents">
    <ul class="sunken-menu-group">
      <li class="tooltipped tooltipped-w" aria-label="Code">
        <a href="/leizongmin/js-xss/tree/v0.1.9" aria-label="Code" class="selected js-selected-navigation-item sunken-menu-item" data-hotkey="g c" data-pjax="true" data-selected-links="repo_source repo_downloads repo_commits repo_releases repo_tags repo_branches /leizongmin/js-xss/tree/v0.1.9">
          <span class="octicon octicon-code"></span> <span class="full-word">Code</span>
          <img alt="" class="mini-loader" height="16" src="https://assets-cdn.github.com/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>

        <li class="tooltipped tooltipped-w" aria-label="Issues">
          <a href="/leizongmin/js-xss/issues" aria-label="Issues" class="js-selected-navigation-item sunken-menu-item js-disable-pjax" data-hotkey="g i" data-selected-links="repo_issues /leizongmin/js-xss/issues">
            <span class="octicon octicon-issue-opened"></span> <span class="full-word">Issues</span>
            <span class='counter'>2</span>
            <img alt="" class="mini-loader" height="16" src="https://assets-cdn.github.com/images/spinners/octocat-spinner-32.gif" width="16" />
</a>        </li>

      <li class="tooltipped tooltipped-w" aria-label="Pull Requests">
        <a href="/leizongmin/js-xss/pulls" aria-label="Pull Requests" class="js-selected-navigation-item sunken-menu-item js-disable-pjax" data-hotkey="g p" data-selected-links="repo_pulls /leizongmin/js-xss/pulls">
            <span class="octicon octicon-git-pull-request"></span> <span class="full-word">Pull Requests</span>
            <span class='counter'>0</span>
            <img alt="" class="mini-loader" height="16" src="https://assets-cdn.github.com/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>


        <li class="tooltipped tooltipped-w" aria-label="Wiki">
          <a href="/leizongmin/js-xss/wiki" aria-label="Wiki" class="js-selected-navigation-item sunken-menu-item js-disable-pjax" data-hotkey="g w" data-selected-links="repo_wiki /leizongmin/js-xss/wiki">
            <span class="octicon octicon-book"></span> <span class="full-word">Wiki</span>
            <img alt="" class="mini-loader" height="16" src="https://assets-cdn.github.com/images/spinners/octocat-spinner-32.gif" width="16" />
</a>        </li>
    </ul>
    <div class="sunken-menu-separator"></div>
    <ul class="sunken-menu-group">

      <li class="tooltipped tooltipped-w" aria-label="Pulse">
        <a href="/leizongmin/js-xss/pulse" aria-label="Pulse" class="js-selected-navigation-item sunken-menu-item" data-pjax="true" data-selected-links="pulse /leizongmin/js-xss/pulse">
          <span class="octicon octicon-pulse"></span> <span class="full-word">Pulse</span>
          <img alt="" class="mini-loader" height="16" src="https://assets-cdn.github.com/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>

      <li class="tooltipped tooltipped-w" aria-label="Graphs">
        <a href="/leizongmin/js-xss/graphs" aria-label="Graphs" class="js-selected-navigation-item sunken-menu-item" data-pjax="true" data-selected-links="repo_graphs repo_contributors /leizongmin/js-xss/graphs">
          <span class="octicon octicon-graph"></span> <span class="full-word">Graphs</span>
          <img alt="" class="mini-loader" height="16" src="https://assets-cdn.github.com/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>

      <li class="tooltipped tooltipped-w" aria-label="Network">
        <a href="/leizongmin/js-xss/network" aria-label="Network" class="js-selected-navigation-item sunken-menu-item js-disable-pjax" data-selected-links="repo_network /leizongmin/js-xss/network">
          <span class="octicon octicon-repo-forked"></span> <span class="full-word">Network</span>
          <img alt="" class="mini-loader" height="16" src="https://assets-cdn.github.com/images/spinners/octocat-spinner-32.gif" width="16" />
</a>      </li>
    </ul>


  </div>
</div>

              <div class="only-with-full-nav">
                

  

<div class="clone-url open"
  data-protocol-type="http"
  data-url="/users/set_protocol?protocol_selector=http&amp;protocol_type=clone">
  <h3><strong>HTTPS</strong> clone URL</h3>
  <div class="clone-url-box">
    <input type="text" class="clone js-url-field"
           value="https://github.com/leizongmin/js-xss.git" readonly="readonly">
    <span class="url-box-clippy">
    <button aria-label="Copy to clipboard" class="js-zeroclipboard minibutton zeroclipboard-button" data-clipboard-text="https://github.com/leizongmin/js-xss.git" data-copied-hint="Copied!" type="button"><span class="octicon octicon-clippy"></span></button>
    </span>
  </div>
</div>

  

<div class="clone-url "
  data-protocol-type="ssh"
  data-url="/users/set_protocol?protocol_selector=ssh&amp;protocol_type=clone">
  <h3><strong>SSH</strong> clone URL</h3>
  <div class="clone-url-box">
    <input type="text" class="clone js-url-field"
           value="git@github.com:leizongmin/js-xss.git" readonly="readonly">
    <span class="url-box-clippy">
    <button aria-label="Copy to clipboard" class="js-zeroclipboard minibutton zeroclipboard-button" data-clipboard-text="git@github.com:leizongmin/js-xss.git" data-copied-hint="Copied!" type="button"><span class="octicon octicon-clippy"></span></button>
    </span>
  </div>
</div>

  

<div class="clone-url "
  data-protocol-type="subversion"
  data-url="/users/set_protocol?protocol_selector=subversion&amp;protocol_type=clone">
  <h3><strong>Subversion</strong> checkout URL</h3>
  <div class="clone-url-box">
    <input type="text" class="clone js-url-field"
           value="https://github.com/leizongmin/js-xss" readonly="readonly">
    <span class="url-box-clippy">
    <button aria-label="Copy to clipboard" class="js-zeroclipboard minibutton zeroclipboard-button" data-clipboard-text="https://github.com/leizongmin/js-xss" data-copied-hint="Copied!" type="button"><span class="octicon octicon-clippy"></span></button>
    </span>
  </div>
</div>


<p class="clone-options">You can clone with
      <a href="#" class="js-clone-selector" data-protocol="http">HTTPS</a>,
      <a href="#" class="js-clone-selector" data-protocol="ssh">SSH</a>,
      or <a href="#" class="js-clone-selector" data-protocol="subversion">Subversion</a>.
  <a href="https://help.github.com/articles/which-remote-url-should-i-use" class="help tooltipped tooltipped-n" aria-label="Get help on which URL is right for you.">
    <span class="octicon octicon-question"></span>
  </a>
</p>


  <a href="http://windows.github.com" class="minibutton sidebar-button" title="Save leizongmin/js-xss to your computer and use it in GitHub Desktop." aria-label="Save leizongmin/js-xss to your computer and use it in GitHub Desktop.">
    <span class="octicon octicon-device-desktop"></span>
    Clone in Desktop
  </a>

                <a href="/leizongmin/js-xss/archive/v0.1.9.zip"
                   class="minibutton sidebar-button"
                   aria-label="Download leizongmin/js-xss as a zip file"
                   title="Download leizongmin/js-xss as a zip file"
                   rel="nofollow">
                  <span class="octicon octicon-cloud-download"></span>
                  Download ZIP
                </a>
              </div>
        </div><!-- /.repository-sidebar -->

        <div id="js-repo-pjax-container" class="repository-content context-loader-container" data-pjax-container>
          


<a href="/leizongmin/js-xss/blob/4492d1d907d5984df49c7137daee0f1eccd1add8/dist/xss.js" class="hidden js-permalink-shortcut" data-hotkey="y">Permalink</a>

<!-- blob contrib key: blob_contributors:v21:725b5fcfdf18ebb711cded830289ad4a -->

<p title="This is a placeholder element" class="js-history-link-replace hidden"></p>

<div class="file-navigation">
  

<div class="select-menu js-menu-container js-select-menu" >
  <span class="minibutton select-menu-button js-menu-target css-truncate" data-hotkey="w"
    data-master-branch="master"
    data-ref="v0.1.9"
    title="v0.1.9"
    role="button" aria-label="Switch branches or tags" tabindex="0" aria-haspopup="true">
    <span class="octicon octicon-tag"></span>
    <i>tag:</i>
    <span class="js-select-button css-truncate-target">v0.1.9</span>
  </span>

  <div class="select-menu-modal-holder js-menu-content js-navigation-container" data-pjax aria-hidden="true">

    <div class="select-menu-modal">
      <div class="select-menu-header">
        <span class="select-menu-title">Switch branches/tags</span>
        <span class="octicon octicon-x js-menu-close" role="button" aria-label="Close"></span>
      </div> <!-- /.select-menu-header -->

      <div class="select-menu-filters">
        <div class="select-menu-text-filter">
          <input type="text" aria-label="Filter branches/tags" id="context-commitish-filter-field" class="js-filterable-field js-navigation-enable" placeholder="Filter branches/tags">
        </div>
        <div class="select-menu-tabs">
          <ul>
            <li class="select-menu-tab">
              <a href="#" data-tab-filter="branches" class="js-select-menu-tab">Branches</a>
            </li>
            <li class="select-menu-tab">
              <a href="#" data-tab-filter="tags" class="js-select-menu-tab">Tags</a>
            </li>
          </ul>
        </div><!-- /.select-menu-tabs -->
      </div><!-- /.select-menu-filters -->

      <div class="select-menu-list select-menu-tab-bucket js-select-menu-tab-bucket" data-tab-filter="branches">

        <div data-filterable-for="context-commitish-filter-field" data-filterable-type="substring">


            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/blob/master/dist/xss.js"
                 data-name="master"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="master">master</a>
            </div> <!-- /.select-menu-item -->
            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/blob/v0.1.x/dist/xss.js"
                 data-name="v0.1.x"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="v0.1.x">v0.1.x</a>
            </div> <!-- /.select-menu-item -->
        </div>

          <div class="select-menu-no-results">Nothing to show</div>
      </div> <!-- /.select-menu-list -->

      <div class="select-menu-list select-menu-tab-bucket js-select-menu-tab-bucket" data-tab-filter="tags">
        <div data-filterable-for="context-commitish-filter-field" data-filterable-type="substring">


            <div class="select-menu-item js-navigation-item selected">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/tree/v0.1.9/dist/xss.js"
                 data-name="v0.1.9"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="v0.1.9">v0.1.9</a>
            </div> <!-- /.select-menu-item -->
            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/tree/v0.1.7/dist/xss.js"
                 data-name="v0.1.7"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="v0.1.7">v0.1.7</a>
            </div> <!-- /.select-menu-item -->
            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/tree/v0.1.6/dist/xss.js"
                 data-name="v0.1.6"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="v0.1.6">v0.1.6</a>
            </div> <!-- /.select-menu-item -->
            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/tree/v0.1.4/dist/xss.js"
                 data-name="v0.1.4"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="v0.1.4">v0.1.4</a>
            </div> <!-- /.select-menu-item -->
            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/tree/v0.1.2/dist/xss.js"
                 data-name="v0.1.2"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="v0.1.2">v0.1.2</a>
            </div> <!-- /.select-menu-item -->
            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/tree/v0.1.0/dist/xss.js"
                 data-name="v0.1.0"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="v0.1.0">v0.1.0</a>
            </div> <!-- /.select-menu-item -->
            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/tree/v0.0.9/dist/xss.js"
                 data-name="v0.0.9"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="v0.0.9">v0.0.9</a>
            </div> <!-- /.select-menu-item -->
            <div class="select-menu-item js-navigation-item ">
              <span class="select-menu-item-icon octicon octicon-check"></span>
              <a href="/leizongmin/js-xss/tree/v0.0.5/dist/xss.js"
                 data-name="v0.0.5"
                 data-skip-pjax="true"
                 rel="nofollow"
                 class="js-navigation-open select-menu-item-text css-truncate-target"
                 title="v0.0.5">v0.0.5</a>
            </div> <!-- /.select-menu-item -->
        </div>

        <div class="select-menu-no-results">Nothing to show</div>
      </div> <!-- /.select-menu-list -->

    </div> <!-- /.select-menu-modal -->
  </div> <!-- /.select-menu-modal-holder -->
</div> <!-- /.select-menu -->

  <div class="button-group right">
    <a href="/leizongmin/js-xss/find/v0.1.9"
          class="js-show-file-finder minibutton empty-icon tooltipped tooltipped-s"
          data-pjax
          data-hotkey="t"
          aria-label="Quickly jump between files">
      <span class="octicon octicon-list-unordered"></span>
    </a>
    <button class="js-zeroclipboard minibutton zeroclipboard-button"
          data-clipboard-text="dist/xss.js"
          aria-label="Copy to clipboard"
          data-copied-hint="Copied!">
      <span class="octicon octicon-clippy"></span>
    </button>
  </div>

  <div class="breadcrumb">
    <span class='repo-root js-repo-root'><span itemscope="" itemtype="http://data-vocabulary.org/Breadcrumb"><a href="/leizongmin/js-xss/tree/v0.1.9" data-branch="v0.1.9" data-direction="back" data-pjax="true" itemscope="url"><span itemprop="title">js-xss</span></a></span></span><span class="separator"> / </span><span itemscope="" itemtype="http://data-vocabulary.org/Breadcrumb"><a href="/leizongmin/js-xss/tree/v0.1.9/dist" data-branch="v0.1.9" data-direction="back" data-pjax="true" itemscope="url"><span itemprop="title">dist</span></a></span><span class="separator"> / </span><strong class="final-path">xss.js</strong>
  </div>
</div>


  <div class="commit commit-loader file-history-tease js-deferred-content" data-url="/leizongmin/js-xss/contributors/v0.1.9/dist/xss.js">
    Fetching contributors…

    <div class="participation">
      <p class="loader-loading"><img alt="" height="16" src="https://assets-cdn.github.com/images/spinners/octocat-spinner-32-EAF2F5.gif" width="16" /></p>
      <p class="loader-error">Cannot retrieve contributors at this time</p>
    </div>
  </div>

<div class="file-box">
  <div class="file">
    <div class="meta clearfix">
      <div class="info file-name">
        <span class="icon"><b class="octicon octicon-file-text"></b></span>
        <span class="mode" title="File Mode">file</span>
        <span class="meta-divider"></span>
          <span>790 lines (716 sloc)</span>
          <span class="meta-divider"></span>
        <span>20.531 kb</span>
      </div>
      <div class="actions">
        <div class="button-group">
              <a class="minibutton disabled tooltipped tooltipped-w" href="#"
                 aria-label="You must be on a branch to make or propose changes to this file">Edit</a>
          <a href="/leizongmin/js-xss/raw/v0.1.9/dist/xss.js" class="minibutton " id="raw-url">Raw</a>
            <a href="/leizongmin/js-xss/blame/v0.1.9/dist/xss.js" class="minibutton js-update-url-with-hash">Blame</a>
          <a href="/leizongmin/js-xss/commits/v0.1.9/dist/xss.js" class="minibutton " rel="nofollow">History</a>
        </div><!-- /.button-group -->
          <a class="minibutton danger disabled empty-icon tooltipped tooltipped-w" href="#"
             aria-label="You must be on a branch to make or propose changes to this file">
          Delete
        </a>
      </div><!-- /.actions -->
    </div>
      
  <div class="blob-wrapper data type-javascript js-blob-data">
       <table class="file-code file-diff tab-size-8">
         <tr class="file-code-line">
           <td class="blob-line-nums">
             <span id="L1" rel="#L1">1</span>
<span id="L2" rel="#L2">2</span>
<span id="L3" rel="#L3">3</span>
<span id="L4" rel="#L4">4</span>
<span id="L5" rel="#L5">5</span>
<span id="L6" rel="#L6">6</span>
<span id="L7" rel="#L7">7</span>
<span id="L8" rel="#L8">8</span>
<span id="L9" rel="#L9">9</span>
<span id="L10" rel="#L10">10</span>
<span id="L11" rel="#L11">11</span>
<span id="L12" rel="#L12">12</span>
<span id="L13" rel="#L13">13</span>
<span id="L14" rel="#L14">14</span>
<span id="L15" rel="#L15">15</span>
<span id="L16" rel="#L16">16</span>
<span id="L17" rel="#L17">17</span>
<span id="L18" rel="#L18">18</span>
<span id="L19" rel="#L19">19</span>
<span id="L20" rel="#L20">20</span>
<span id="L21" rel="#L21">21</span>
<span id="L22" rel="#L22">22</span>
<span id="L23" rel="#L23">23</span>
<span id="L24" rel="#L24">24</span>
<span id="L25" rel="#L25">25</span>
<span id="L26" rel="#L26">26</span>
<span id="L27" rel="#L27">27</span>
<span id="L28" rel="#L28">28</span>
<span id="L29" rel="#L29">29</span>
<span id="L30" rel="#L30">30</span>
<span id="L31" rel="#L31">31</span>
<span id="L32" rel="#L32">32</span>
<span id="L33" rel="#L33">33</span>
<span id="L34" rel="#L34">34</span>
<span id="L35" rel="#L35">35</span>
<span id="L36" rel="#L36">36</span>
<span id="L37" rel="#L37">37</span>
<span id="L38" rel="#L38">38</span>
<span id="L39" rel="#L39">39</span>
<span id="L40" rel="#L40">40</span>
<span id="L41" rel="#L41">41</span>
<span id="L42" rel="#L42">42</span>
<span id="L43" rel="#L43">43</span>
<span id="L44" rel="#L44">44</span>
<span id="L45" rel="#L45">45</span>
<span id="L46" rel="#L46">46</span>
<span id="L47" rel="#L47">47</span>
<span id="L48" rel="#L48">48</span>
<span id="L49" rel="#L49">49</span>
<span id="L50" rel="#L50">50</span>
<span id="L51" rel="#L51">51</span>
<span id="L52" rel="#L52">52</span>
<span id="L53" rel="#L53">53</span>
<span id="L54" rel="#L54">54</span>
<span id="L55" rel="#L55">55</span>
<span id="L56" rel="#L56">56</span>
<span id="L57" rel="#L57">57</span>
<span id="L58" rel="#L58">58</span>
<span id="L59" rel="#L59">59</span>
<span id="L60" rel="#L60">60</span>
<span id="L61" rel="#L61">61</span>
<span id="L62" rel="#L62">62</span>
<span id="L63" rel="#L63">63</span>
<span id="L64" rel="#L64">64</span>
<span id="L65" rel="#L65">65</span>
<span id="L66" rel="#L66">66</span>
<span id="L67" rel="#L67">67</span>
<span id="L68" rel="#L68">68</span>
<span id="L69" rel="#L69">69</span>
<span id="L70" rel="#L70">70</span>
<span id="L71" rel="#L71">71</span>
<span id="L72" rel="#L72">72</span>
<span id="L73" rel="#L73">73</span>
<span id="L74" rel="#L74">74</span>
<span id="L75" rel="#L75">75</span>
<span id="L76" rel="#L76">76</span>
<span id="L77" rel="#L77">77</span>
<span id="L78" rel="#L78">78</span>
<span id="L79" rel="#L79">79</span>
<span id="L80" rel="#L80">80</span>
<span id="L81" rel="#L81">81</span>
<span id="L82" rel="#L82">82</span>
<span id="L83" rel="#L83">83</span>
<span id="L84" rel="#L84">84</span>
<span id="L85" rel="#L85">85</span>
<span id="L86" rel="#L86">86</span>
<span id="L87" rel="#L87">87</span>
<span id="L88" rel="#L88">88</span>
<span id="L89" rel="#L89">89</span>
<span id="L90" rel="#L90">90</span>
<span id="L91" rel="#L91">91</span>
<span id="L92" rel="#L92">92</span>
<span id="L93" rel="#L93">93</span>
<span id="L94" rel="#L94">94</span>
<span id="L95" rel="#L95">95</span>
<span id="L96" rel="#L96">96</span>
<span id="L97" rel="#L97">97</span>
<span id="L98" rel="#L98">98</span>
<span id="L99" rel="#L99">99</span>
<span id="L100" rel="#L100">100</span>
<span id="L101" rel="#L101">101</span>
<span id="L102" rel="#L102">102</span>
<span id="L103" rel="#L103">103</span>
<span id="L104" rel="#L104">104</span>
<span id="L105" rel="#L105">105</span>
<span id="L106" rel="#L106">106</span>
<span id="L107" rel="#L107">107</span>
<span id="L108" rel="#L108">108</span>
<span id="L109" rel="#L109">109</span>
<span id="L110" rel="#L110">110</span>
<span id="L111" rel="#L111">111</span>
<span id="L112" rel="#L112">112</span>
<span id="L113" rel="#L113">113</span>
<span id="L114" rel="#L114">114</span>
<span id="L115" rel="#L115">115</span>
<span id="L116" rel="#L116">116</span>
<span id="L117" rel="#L117">117</span>
<span id="L118" rel="#L118">118</span>
<span id="L119" rel="#L119">119</span>
<span id="L120" rel="#L120">120</span>
<span id="L121" rel="#L121">121</span>
<span id="L122" rel="#L122">122</span>
<span id="L123" rel="#L123">123</span>
<span id="L124" rel="#L124">124</span>
<span id="L125" rel="#L125">125</span>
<span id="L126" rel="#L126">126</span>
<span id="L127" rel="#L127">127</span>
<span id="L128" rel="#L128">128</span>
<span id="L129" rel="#L129">129</span>
<span id="L130" rel="#L130">130</span>
<span id="L131" rel="#L131">131</span>
<span id="L132" rel="#L132">132</span>
<span id="L133" rel="#L133">133</span>
<span id="L134" rel="#L134">134</span>
<span id="L135" rel="#L135">135</span>
<span id="L136" rel="#L136">136</span>
<span id="L137" rel="#L137">137</span>
<span id="L138" rel="#L138">138</span>
<span id="L139" rel="#L139">139</span>
<span id="L140" rel="#L140">140</span>
<span id="L141" rel="#L141">141</span>
<span id="L142" rel="#L142">142</span>
<span id="L143" rel="#L143">143</span>
<span id="L144" rel="#L144">144</span>
<span id="L145" rel="#L145">145</span>
<span id="L146" rel="#L146">146</span>
<span id="L147" rel="#L147">147</span>
<span id="L148" rel="#L148">148</span>
<span id="L149" rel="#L149">149</span>
<span id="L150" rel="#L150">150</span>
<span id="L151" rel="#L151">151</span>
<span id="L152" rel="#L152">152</span>
<span id="L153" rel="#L153">153</span>
<span id="L154" rel="#L154">154</span>
<span id="L155" rel="#L155">155</span>
<span id="L156" rel="#L156">156</span>
<span id="L157" rel="#L157">157</span>
<span id="L158" rel="#L158">158</span>
<span id="L159" rel="#L159">159</span>
<span id="L160" rel="#L160">160</span>
<span id="L161" rel="#L161">161</span>
<span id="L162" rel="#L162">162</span>
<span id="L163" rel="#L163">163</span>
<span id="L164" rel="#L164">164</span>
<span id="L165" rel="#L165">165</span>
<span id="L166" rel="#L166">166</span>
<span id="L167" rel="#L167">167</span>
<span id="L168" rel="#L168">168</span>
<span id="L169" rel="#L169">169</span>
<span id="L170" rel="#L170">170</span>
<span id="L171" rel="#L171">171</span>
<span id="L172" rel="#L172">172</span>
<span id="L173" rel="#L173">173</span>
<span id="L174" rel="#L174">174</span>
<span id="L175" rel="#L175">175</span>
<span id="L176" rel="#L176">176</span>
<span id="L177" rel="#L177">177</span>
<span id="L178" rel="#L178">178</span>
<span id="L179" rel="#L179">179</span>
<span id="L180" rel="#L180">180</span>
<span id="L181" rel="#L181">181</span>
<span id="L182" rel="#L182">182</span>
<span id="L183" rel="#L183">183</span>
<span id="L184" rel="#L184">184</span>
<span id="L185" rel="#L185">185</span>
<span id="L186" rel="#L186">186</span>
<span id="L187" rel="#L187">187</span>
<span id="L188" rel="#L188">188</span>
<span id="L189" rel="#L189">189</span>
<span id="L190" rel="#L190">190</span>
<span id="L191" rel="#L191">191</span>
<span id="L192" rel="#L192">192</span>
<span id="L193" rel="#L193">193</span>
<span id="L194" rel="#L194">194</span>
<span id="L195" rel="#L195">195</span>
<span id="L196" rel="#L196">196</span>
<span id="L197" rel="#L197">197</span>
<span id="L198" rel="#L198">198</span>
<span id="L199" rel="#L199">199</span>
<span id="L200" rel="#L200">200</span>
<span id="L201" rel="#L201">201</span>
<span id="L202" rel="#L202">202</span>
<span id="L203" rel="#L203">203</span>
<span id="L204" rel="#L204">204</span>
<span id="L205" rel="#L205">205</span>
<span id="L206" rel="#L206">206</span>
<span id="L207" rel="#L207">207</span>
<span id="L208" rel="#L208">208</span>
<span id="L209" rel="#L209">209</span>
<span id="L210" rel="#L210">210</span>
<span id="L211" rel="#L211">211</span>
<span id="L212" rel="#L212">212</span>
<span id="L213" rel="#L213">213</span>
<span id="L214" rel="#L214">214</span>
<span id="L215" rel="#L215">215</span>
<span id="L216" rel="#L216">216</span>
<span id="L217" rel="#L217">217</span>
<span id="L218" rel="#L218">218</span>
<span id="L219" rel="#L219">219</span>
<span id="L220" rel="#L220">220</span>
<span id="L221" rel="#L221">221</span>
<span id="L222" rel="#L222">222</span>
<span id="L223" rel="#L223">223</span>
<span id="L224" rel="#L224">224</span>
<span id="L225" rel="#L225">225</span>
<span id="L226" rel="#L226">226</span>
<span id="L227" rel="#L227">227</span>
<span id="L228" rel="#L228">228</span>
<span id="L229" rel="#L229">229</span>
<span id="L230" rel="#L230">230</span>
<span id="L231" rel="#L231">231</span>
<span id="L232" rel="#L232">232</span>
<span id="L233" rel="#L233">233</span>
<span id="L234" rel="#L234">234</span>
<span id="L235" rel="#L235">235</span>
<span id="L236" rel="#L236">236</span>
<span id="L237" rel="#L237">237</span>
<span id="L238" rel="#L238">238</span>
<span id="L239" rel="#L239">239</span>
<span id="L240" rel="#L240">240</span>
<span id="L241" rel="#L241">241</span>
<span id="L242" rel="#L242">242</span>
<span id="L243" rel="#L243">243</span>
<span id="L244" rel="#L244">244</span>
<span id="L245" rel="#L245">245</span>
<span id="L246" rel="#L246">246</span>
<span id="L247" rel="#L247">247</span>
<span id="L248" rel="#L248">248</span>
<span id="L249" rel="#L249">249</span>
<span id="L250" rel="#L250">250</span>
<span id="L251" rel="#L251">251</span>
<span id="L252" rel="#L252">252</span>
<span id="L253" rel="#L253">253</span>
<span id="L254" rel="#L254">254</span>
<span id="L255" rel="#L255">255</span>
<span id="L256" rel="#L256">256</span>
<span id="L257" rel="#L257">257</span>
<span id="L258" rel="#L258">258</span>
<span id="L259" rel="#L259">259</span>
<span id="L260" rel="#L260">260</span>
<span id="L261" rel="#L261">261</span>
<span id="L262" rel="#L262">262</span>
<span id="L263" rel="#L263">263</span>
<span id="L264" rel="#L264">264</span>
<span id="L265" rel="#L265">265</span>
<span id="L266" rel="#L266">266</span>
<span id="L267" rel="#L267">267</span>
<span id="L268" rel="#L268">268</span>
<span id="L269" rel="#L269">269</span>
<span id="L270" rel="#L270">270</span>
<span id="L271" rel="#L271">271</span>
<span id="L272" rel="#L272">272</span>
<span id="L273" rel="#L273">273</span>
<span id="L274" rel="#L274">274</span>
<span id="L275" rel="#L275">275</span>
<span id="L276" rel="#L276">276</span>
<span id="L277" rel="#L277">277</span>
<span id="L278" rel="#L278">278</span>
<span id="L279" rel="#L279">279</span>
<span id="L280" rel="#L280">280</span>
<span id="L281" rel="#L281">281</span>
<span id="L282" rel="#L282">282</span>
<span id="L283" rel="#L283">283</span>
<span id="L284" rel="#L284">284</span>
<span id="L285" rel="#L285">285</span>
<span id="L286" rel="#L286">286</span>
<span id="L287" rel="#L287">287</span>
<span id="L288" rel="#L288">288</span>
<span id="L289" rel="#L289">289</span>
<span id="L290" rel="#L290">290</span>
<span id="L291" rel="#L291">291</span>
<span id="L292" rel="#L292">292</span>
<span id="L293" rel="#L293">293</span>
<span id="L294" rel="#L294">294</span>
<span id="L295" rel="#L295">295</span>
<span id="L296" rel="#L296">296</span>
<span id="L297" rel="#L297">297</span>
<span id="L298" rel="#L298">298</span>
<span id="L299" rel="#L299">299</span>
<span id="L300" rel="#L300">300</span>
<span id="L301" rel="#L301">301</span>
<span id="L302" rel="#L302">302</span>
<span id="L303" rel="#L303">303</span>
<span id="L304" rel="#L304">304</span>
<span id="L305" rel="#L305">305</span>
<span id="L306" rel="#L306">306</span>
<span id="L307" rel="#L307">307</span>
<span id="L308" rel="#L308">308</span>
<span id="L309" rel="#L309">309</span>
<span id="L310" rel="#L310">310</span>
<span id="L311" rel="#L311">311</span>
<span id="L312" rel="#L312">312</span>
<span id="L313" rel="#L313">313</span>
<span id="L314" rel="#L314">314</span>
<span id="L315" rel="#L315">315</span>
<span id="L316" rel="#L316">316</span>
<span id="L317" rel="#L317">317</span>
<span id="L318" rel="#L318">318</span>
<span id="L319" rel="#L319">319</span>
<span id="L320" rel="#L320">320</span>
<span id="L321" rel="#L321">321</span>
<span id="L322" rel="#L322">322</span>
<span id="L323" rel="#L323">323</span>
<span id="L324" rel="#L324">324</span>
<span id="L325" rel="#L325">325</span>
<span id="L326" rel="#L326">326</span>
<span id="L327" rel="#L327">327</span>
<span id="L328" rel="#L328">328</span>
<span id="L329" rel="#L329">329</span>
<span id="L330" rel="#L330">330</span>
<span id="L331" rel="#L331">331</span>
<span id="L332" rel="#L332">332</span>
<span id="L333" rel="#L333">333</span>
<span id="L334" rel="#L334">334</span>
<span id="L335" rel="#L335">335</span>
<span id="L336" rel="#L336">336</span>
<span id="L337" rel="#L337">337</span>
<span id="L338" rel="#L338">338</span>
<span id="L339" rel="#L339">339</span>
<span id="L340" rel="#L340">340</span>
<span id="L341" rel="#L341">341</span>
<span id="L342" rel="#L342">342</span>
<span id="L343" rel="#L343">343</span>
<span id="L344" rel="#L344">344</span>
<span id="L345" rel="#L345">345</span>
<span id="L346" rel="#L346">346</span>
<span id="L347" rel="#L347">347</span>
<span id="L348" rel="#L348">348</span>
<span id="L349" rel="#L349">349</span>
<span id="L350" rel="#L350">350</span>
<span id="L351" rel="#L351">351</span>
<span id="L352" rel="#L352">352</span>
<span id="L353" rel="#L353">353</span>
<span id="L354" rel="#L354">354</span>
<span id="L355" rel="#L355">355</span>
<span id="L356" rel="#L356">356</span>
<span id="L357" rel="#L357">357</span>
<span id="L358" rel="#L358">358</span>
<span id="L359" rel="#L359">359</span>
<span id="L360" rel="#L360">360</span>
<span id="L361" rel="#L361">361</span>
<span id="L362" rel="#L362">362</span>
<span id="L363" rel="#L363">363</span>
<span id="L364" rel="#L364">364</span>
<span id="L365" rel="#L365">365</span>
<span id="L366" rel="#L366">366</span>
<span id="L367" rel="#L367">367</span>
<span id="L368" rel="#L368">368</span>
<span id="L369" rel="#L369">369</span>
<span id="L370" rel="#L370">370</span>
<span id="L371" rel="#L371">371</span>
<span id="L372" rel="#L372">372</span>
<span id="L373" rel="#L373">373</span>
<span id="L374" rel="#L374">374</span>
<span id="L375" rel="#L375">375</span>
<span id="L376" rel="#L376">376</span>
<span id="L377" rel="#L377">377</span>
<span id="L378" rel="#L378">378</span>
<span id="L379" rel="#L379">379</span>
<span id="L380" rel="#L380">380</span>
<span id="L381" rel="#L381">381</span>
<span id="L382" rel="#L382">382</span>
<span id="L383" rel="#L383">383</span>
<span id="L384" rel="#L384">384</span>
<span id="L385" rel="#L385">385</span>
<span id="L386" rel="#L386">386</span>
<span id="L387" rel="#L387">387</span>
<span id="L388" rel="#L388">388</span>
<span id="L389" rel="#L389">389</span>
<span id="L390" rel="#L390">390</span>
<span id="L391" rel="#L391">391</span>
<span id="L392" rel="#L392">392</span>
<span id="L393" rel="#L393">393</span>
<span id="L394" rel="#L394">394</span>
<span id="L395" rel="#L395">395</span>
<span id="L396" rel="#L396">396</span>
<span id="L397" rel="#L397">397</span>
<span id="L398" rel="#L398">398</span>
<span id="L399" rel="#L399">399</span>
<span id="L400" rel="#L400">400</span>
<span id="L401" rel="#L401">401</span>
<span id="L402" rel="#L402">402</span>
<span id="L403" rel="#L403">403</span>
<span id="L404" rel="#L404">404</span>
<span id="L405" rel="#L405">405</span>
<span id="L406" rel="#L406">406</span>
<span id="L407" rel="#L407">407</span>
<span id="L408" rel="#L408">408</span>
<span id="L409" rel="#L409">409</span>
<span id="L410" rel="#L410">410</span>
<span id="L411" rel="#L411">411</span>
<span id="L412" rel="#L412">412</span>
<span id="L413" rel="#L413">413</span>
<span id="L414" rel="#L414">414</span>
<span id="L415" rel="#L415">415</span>
<span id="L416" rel="#L416">416</span>
<span id="L417" rel="#L417">417</span>
<span id="L418" rel="#L418">418</span>
<span id="L419" rel="#L419">419</span>
<span id="L420" rel="#L420">420</span>
<span id="L421" rel="#L421">421</span>
<span id="L422" rel="#L422">422</span>
<span id="L423" rel="#L423">423</span>
<span id="L424" rel="#L424">424</span>
<span id="L425" rel="#L425">425</span>
<span id="L426" rel="#L426">426</span>
<span id="L427" rel="#L427">427</span>
<span id="L428" rel="#L428">428</span>
<span id="L429" rel="#L429">429</span>
<span id="L430" rel="#L430">430</span>
<span id="L431" rel="#L431">431</span>
<span id="L432" rel="#L432">432</span>
<span id="L433" rel="#L433">433</span>
<span id="L434" rel="#L434">434</span>
<span id="L435" rel="#L435">435</span>
<span id="L436" rel="#L436">436</span>
<span id="L437" rel="#L437">437</span>
<span id="L438" rel="#L438">438</span>
<span id="L439" rel="#L439">439</span>
<span id="L440" rel="#L440">440</span>
<span id="L441" rel="#L441">441</span>
<span id="L442" rel="#L442">442</span>
<span id="L443" rel="#L443">443</span>
<span id="L444" rel="#L444">444</span>
<span id="L445" rel="#L445">445</span>
<span id="L446" rel="#L446">446</span>
<span id="L447" rel="#L447">447</span>
<span id="L448" rel="#L448">448</span>
<span id="L449" rel="#L449">449</span>
<span id="L450" rel="#L450">450</span>
<span id="L451" rel="#L451">451</span>
<span id="L452" rel="#L452">452</span>
<span id="L453" rel="#L453">453</span>
<span id="L454" rel="#L454">454</span>
<span id="L455" rel="#L455">455</span>
<span id="L456" rel="#L456">456</span>
<span id="L457" rel="#L457">457</span>
<span id="L458" rel="#L458">458</span>
<span id="L459" rel="#L459">459</span>
<span id="L460" rel="#L460">460</span>
<span id="L461" rel="#L461">461</span>
<span id="L462" rel="#L462">462</span>
<span id="L463" rel="#L463">463</span>
<span id="L464" rel="#L464">464</span>
<span id="L465" rel="#L465">465</span>
<span id="L466" rel="#L466">466</span>
<span id="L467" rel="#L467">467</span>
<span id="L468" rel="#L468">468</span>
<span id="L469" rel="#L469">469</span>
<span id="L470" rel="#L470">470</span>
<span id="L471" rel="#L471">471</span>
<span id="L472" rel="#L472">472</span>
<span id="L473" rel="#L473">473</span>
<span id="L474" rel="#L474">474</span>
<span id="L475" rel="#L475">475</span>
<span id="L476" rel="#L476">476</span>
<span id="L477" rel="#L477">477</span>
<span id="L478" rel="#L478">478</span>
<span id="L479" rel="#L479">479</span>
<span id="L480" rel="#L480">480</span>
<span id="L481" rel="#L481">481</span>
<span id="L482" rel="#L482">482</span>
<span id="L483" rel="#L483">483</span>
<span id="L484" rel="#L484">484</span>
<span id="L485" rel="#L485">485</span>
<span id="L486" rel="#L486">486</span>
<span id="L487" rel="#L487">487</span>
<span id="L488" rel="#L488">488</span>
<span id="L489" rel="#L489">489</span>
<span id="L490" rel="#L490">490</span>
<span id="L491" rel="#L491">491</span>
<span id="L492" rel="#L492">492</span>
<span id="L493" rel="#L493">493</span>
<span id="L494" rel="#L494">494</span>
<span id="L495" rel="#L495">495</span>
<span id="L496" rel="#L496">496</span>
<span id="L497" rel="#L497">497</span>
<span id="L498" rel="#L498">498</span>
<span id="L499" rel="#L499">499</span>
<span id="L500" rel="#L500">500</span>
<span id="L501" rel="#L501">501</span>
<span id="L502" rel="#L502">502</span>
<span id="L503" rel="#L503">503</span>
<span id="L504" rel="#L504">504</span>
<span id="L505" rel="#L505">505</span>
<span id="L506" rel="#L506">506</span>
<span id="L507" rel="#L507">507</span>
<span id="L508" rel="#L508">508</span>
<span id="L509" rel="#L509">509</span>
<span id="L510" rel="#L510">510</span>
<span id="L511" rel="#L511">511</span>
<span id="L512" rel="#L512">512</span>
<span id="L513" rel="#L513">513</span>
<span id="L514" rel="#L514">514</span>
<span id="L515" rel="#L515">515</span>
<span id="L516" rel="#L516">516</span>
<span id="L517" rel="#L517">517</span>
<span id="L518" rel="#L518">518</span>
<span id="L519" rel="#L519">519</span>
<span id="L520" rel="#L520">520</span>
<span id="L521" rel="#L521">521</span>
<span id="L522" rel="#L522">522</span>
<span id="L523" rel="#L523">523</span>
<span id="L524" rel="#L524">524</span>
<span id="L525" rel="#L525">525</span>
<span id="L526" rel="#L526">526</span>
<span id="L527" rel="#L527">527</span>
<span id="L528" rel="#L528">528</span>
<span id="L529" rel="#L529">529</span>
<span id="L530" rel="#L530">530</span>
<span id="L531" rel="#L531">531</span>
<span id="L532" rel="#L532">532</span>
<span id="L533" rel="#L533">533</span>
<span id="L534" rel="#L534">534</span>
<span id="L535" rel="#L535">535</span>
<span id="L536" rel="#L536">536</span>
<span id="L537" rel="#L537">537</span>
<span id="L538" rel="#L538">538</span>
<span id="L539" rel="#L539">539</span>
<span id="L540" rel="#L540">540</span>
<span id="L541" rel="#L541">541</span>
<span id="L542" rel="#L542">542</span>
<span id="L543" rel="#L543">543</span>
<span id="L544" rel="#L544">544</span>
<span id="L545" rel="#L545">545</span>
<span id="L546" rel="#L546">546</span>
<span id="L547" rel="#L547">547</span>
<span id="L548" rel="#L548">548</span>
<span id="L549" rel="#L549">549</span>
<span id="L550" rel="#L550">550</span>
<span id="L551" rel="#L551">551</span>
<span id="L552" rel="#L552">552</span>
<span id="L553" rel="#L553">553</span>
<span id="L554" rel="#L554">554</span>
<span id="L555" rel="#L555">555</span>
<span id="L556" rel="#L556">556</span>
<span id="L557" rel="#L557">557</span>
<span id="L558" rel="#L558">558</span>
<span id="L559" rel="#L559">559</span>
<span id="L560" rel="#L560">560</span>
<span id="L561" rel="#L561">561</span>
<span id="L562" rel="#L562">562</span>
<span id="L563" rel="#L563">563</span>
<span id="L564" rel="#L564">564</span>
<span id="L565" rel="#L565">565</span>
<span id="L566" rel="#L566">566</span>
<span id="L567" rel="#L567">567</span>
<span id="L568" rel="#L568">568</span>
<span id="L569" rel="#L569">569</span>
<span id="L570" rel="#L570">570</span>
<span id="L571" rel="#L571">571</span>
<span id="L572" rel="#L572">572</span>
<span id="L573" rel="#L573">573</span>
<span id="L574" rel="#L574">574</span>
<span id="L575" rel="#L575">575</span>
<span id="L576" rel="#L576">576</span>
<span id="L577" rel="#L577">577</span>
<span id="L578" rel="#L578">578</span>
<span id="L579" rel="#L579">579</span>
<span id="L580" rel="#L580">580</span>
<span id="L581" rel="#L581">581</span>
<span id="L582" rel="#L582">582</span>
<span id="L583" rel="#L583">583</span>
<span id="L584" rel="#L584">584</span>
<span id="L585" rel="#L585">585</span>
<span id="L586" rel="#L586">586</span>
<span id="L587" rel="#L587">587</span>
<span id="L588" rel="#L588">588</span>
<span id="L589" rel="#L589">589</span>
<span id="L590" rel="#L590">590</span>
<span id="L591" rel="#L591">591</span>
<span id="L592" rel="#L592">592</span>
<span id="L593" rel="#L593">593</span>
<span id="L594" rel="#L594">594</span>
<span id="L595" rel="#L595">595</span>
<span id="L596" rel="#L596">596</span>
<span id="L597" rel="#L597">597</span>
<span id="L598" rel="#L598">598</span>
<span id="L599" rel="#L599">599</span>
<span id="L600" rel="#L600">600</span>
<span id="L601" rel="#L601">601</span>
<span id="L602" rel="#L602">602</span>
<span id="L603" rel="#L603">603</span>
<span id="L604" rel="#L604">604</span>
<span id="L605" rel="#L605">605</span>
<span id="L606" rel="#L606">606</span>
<span id="L607" rel="#L607">607</span>
<span id="L608" rel="#L608">608</span>
<span id="L609" rel="#L609">609</span>
<span id="L610" rel="#L610">610</span>
<span id="L611" rel="#L611">611</span>
<span id="L612" rel="#L612">612</span>
<span id="L613" rel="#L613">613</span>
<span id="L614" rel="#L614">614</span>
<span id="L615" rel="#L615">615</span>
<span id="L616" rel="#L616">616</span>
<span id="L617" rel="#L617">617</span>
<span id="L618" rel="#L618">618</span>
<span id="L619" rel="#L619">619</span>
<span id="L620" rel="#L620">620</span>
<span id="L621" rel="#L621">621</span>
<span id="L622" rel="#L622">622</span>
<span id="L623" rel="#L623">623</span>
<span id="L624" rel="#L624">624</span>
<span id="L625" rel="#L625">625</span>
<span id="L626" rel="#L626">626</span>
<span id="L627" rel="#L627">627</span>
<span id="L628" rel="#L628">628</span>
<span id="L629" rel="#L629">629</span>
<span id="L630" rel="#L630">630</span>
<span id="L631" rel="#L631">631</span>
<span id="L632" rel="#L632">632</span>
<span id="L633" rel="#L633">633</span>
<span id="L634" rel="#L634">634</span>
<span id="L635" rel="#L635">635</span>
<span id="L636" rel="#L636">636</span>
<span id="L637" rel="#L637">637</span>
<span id="L638" rel="#L638">638</span>
<span id="L639" rel="#L639">639</span>
<span id="L640" rel="#L640">640</span>
<span id="L641" rel="#L641">641</span>
<span id="L642" rel="#L642">642</span>
<span id="L643" rel="#L643">643</span>
<span id="L644" rel="#L644">644</span>
<span id="L645" rel="#L645">645</span>
<span id="L646" rel="#L646">646</span>
<span id="L647" rel="#L647">647</span>
<span id="L648" rel="#L648">648</span>
<span id="L649" rel="#L649">649</span>
<span id="L650" rel="#L650">650</span>
<span id="L651" rel="#L651">651</span>
<span id="L652" rel="#L652">652</span>
<span id="L653" rel="#L653">653</span>
<span id="L654" rel="#L654">654</span>
<span id="L655" rel="#L655">655</span>
<span id="L656" rel="#L656">656</span>
<span id="L657" rel="#L657">657</span>
<span id="L658" rel="#L658">658</span>
<span id="L659" rel="#L659">659</span>
<span id="L660" rel="#L660">660</span>
<span id="L661" rel="#L661">661</span>
<span id="L662" rel="#L662">662</span>
<span id="L663" rel="#L663">663</span>
<span id="L664" rel="#L664">664</span>
<span id="L665" rel="#L665">665</span>
<span id="L666" rel="#L666">666</span>
<span id="L667" rel="#L667">667</span>
<span id="L668" rel="#L668">668</span>
<span id="L669" rel="#L669">669</span>
<span id="L670" rel="#L670">670</span>
<span id="L671" rel="#L671">671</span>
<span id="L672" rel="#L672">672</span>
<span id="L673" rel="#L673">673</span>
<span id="L674" rel="#L674">674</span>
<span id="L675" rel="#L675">675</span>
<span id="L676" rel="#L676">676</span>
<span id="L677" rel="#L677">677</span>
<span id="L678" rel="#L678">678</span>
<span id="L679" rel="#L679">679</span>
<span id="L680" rel="#L680">680</span>
<span id="L681" rel="#L681">681</span>
<span id="L682" rel="#L682">682</span>
<span id="L683" rel="#L683">683</span>
<span id="L684" rel="#L684">684</span>
<span id="L685" rel="#L685">685</span>
<span id="L686" rel="#L686">686</span>
<span id="L687" rel="#L687">687</span>
<span id="L688" rel="#L688">688</span>
<span id="L689" rel="#L689">689</span>
<span id="L690" rel="#L690">690</span>
<span id="L691" rel="#L691">691</span>
<span id="L692" rel="#L692">692</span>
<span id="L693" rel="#L693">693</span>
<span id="L694" rel="#L694">694</span>
<span id="L695" rel="#L695">695</span>
<span id="L696" rel="#L696">696</span>
<span id="L697" rel="#L697">697</span>
<span id="L698" rel="#L698">698</span>
<span id="L699" rel="#L699">699</span>
<span id="L700" rel="#L700">700</span>
<span id="L701" rel="#L701">701</span>
<span id="L702" rel="#L702">702</span>
<span id="L703" rel="#L703">703</span>
<span id="L704" rel="#L704">704</span>
<span id="L705" rel="#L705">705</span>
<span id="L706" rel="#L706">706</span>
<span id="L707" rel="#L707">707</span>
<span id="L708" rel="#L708">708</span>
<span id="L709" rel="#L709">709</span>
<span id="L710" rel="#L710">710</span>
<span id="L711" rel="#L711">711</span>
<span id="L712" rel="#L712">712</span>
<span id="L713" rel="#L713">713</span>
<span id="L714" rel="#L714">714</span>
<span id="L715" rel="#L715">715</span>
<span id="L716" rel="#L716">716</span>
<span id="L717" rel="#L717">717</span>
<span id="L718" rel="#L718">718</span>
<span id="L719" rel="#L719">719</span>
<span id="L720" rel="#L720">720</span>
<span id="L721" rel="#L721">721</span>
<span id="L722" rel="#L722">722</span>
<span id="L723" rel="#L723">723</span>
<span id="L724" rel="#L724">724</span>
<span id="L725" rel="#L725">725</span>
<span id="L726" rel="#L726">726</span>
<span id="L727" rel="#L727">727</span>
<span id="L728" rel="#L728">728</span>
<span id="L729" rel="#L729">729</span>
<span id="L730" rel="#L730">730</span>
<span id="L731" rel="#L731">731</span>
<span id="L732" rel="#L732">732</span>
<span id="L733" rel="#L733">733</span>
<span id="L734" rel="#L734">734</span>
<span id="L735" rel="#L735">735</span>
<span id="L736" rel="#L736">736</span>
<span id="L737" rel="#L737">737</span>
<span id="L738" rel="#L738">738</span>
<span id="L739" rel="#L739">739</span>
<span id="L740" rel="#L740">740</span>
<span id="L741" rel="#L741">741</span>
<span id="L742" rel="#L742">742</span>
<span id="L743" rel="#L743">743</span>
<span id="L744" rel="#L744">744</span>
<span id="L745" rel="#L745">745</span>
<span id="L746" rel="#L746">746</span>
<span id="L747" rel="#L747">747</span>
<span id="L748" rel="#L748">748</span>
<span id="L749" rel="#L749">749</span>
<span id="L750" rel="#L750">750</span>
<span id="L751" rel="#L751">751</span>
<span id="L752" rel="#L752">752</span>
<span id="L753" rel="#L753">753</span>
<span id="L754" rel="#L754">754</span>
<span id="L755" rel="#L755">755</span>
<span id="L756" rel="#L756">756</span>
<span id="L757" rel="#L757">757</span>
<span id="L758" rel="#L758">758</span>
<span id="L759" rel="#L759">759</span>
<span id="L760" rel="#L760">760</span>
<span id="L761" rel="#L761">761</span>
<span id="L762" rel="#L762">762</span>
<span id="L763" rel="#L763">763</span>
<span id="L764" rel="#L764">764</span>
<span id="L765" rel="#L765">765</span>
<span id="L766" rel="#L766">766</span>
<span id="L767" rel="#L767">767</span>
<span id="L768" rel="#L768">768</span>
<span id="L769" rel="#L769">769</span>
<span id="L770" rel="#L770">770</span>
<span id="L771" rel="#L771">771</span>
<span id="L772" rel="#L772">772</span>
<span id="L773" rel="#L773">773</span>
<span id="L774" rel="#L774">774</span>
<span id="L775" rel="#L775">775</span>
<span id="L776" rel="#L776">776</span>
<span id="L777" rel="#L777">777</span>
<span id="L778" rel="#L778">778</span>
<span id="L779" rel="#L779">779</span>
<span id="L780" rel="#L780">780</span>
<span id="L781" rel="#L781">781</span>
<span id="L782" rel="#L782">782</span>
<span id="L783" rel="#L783">783</span>
<span id="L784" rel="#L784">784</span>
<span id="L785" rel="#L785">785</span>
<span id="L786" rel="#L786">786</span>
<span id="L787" rel="#L787">787</span>
<span id="L788" rel="#L788">788</span>
<span id="L789" rel="#L789">789</span>
<span id="L790" rel="#L790">790</span>

           </td>
           <td class="blob-line-code"><div class="code-body highlight"><pre><div class='line' id='LC1'><span class="p">(</span><span class="kd">function</span> <span class="nx">e</span><span class="p">(</span><span class="nx">t</span><span class="p">,</span><span class="nx">n</span><span class="p">,</span><span class="nx">r</span><span class="p">){</span><span class="kd">function</span> <span class="nx">s</span><span class="p">(</span><span class="nx">o</span><span class="p">,</span><span class="nx">u</span><span class="p">){</span><span class="k">if</span><span class="p">(</span><span class="o">!</span><span class="nx">n</span><span class="p">[</span><span class="nx">o</span><span class="p">]){</span><span class="k">if</span><span class="p">(</span><span class="o">!</span><span class="nx">t</span><span class="p">[</span><span class="nx">o</span><span class="p">]){</span><span class="kd">var</span> <span class="nx">a</span><span class="o">=</span><span class="k">typeof</span> <span class="nx">require</span><span class="o">==</span><span class="s2">&quot;function&quot;</span><span class="o">&amp;&amp;</span><span class="nx">require</span><span class="p">;</span><span class="k">if</span><span class="p">(</span><span class="o">!</span><span class="nx">u</span><span class="o">&amp;&amp;</span><span class="nx">a</span><span class="p">)</span><span class="k">return</span> <span class="nx">a</span><span class="p">(</span><span class="nx">o</span><span class="p">,</span><span class="o">!</span><span class="mi">0</span><span class="p">);</span><span class="k">if</span><span class="p">(</span><span class="nx">i</span><span class="p">)</span><span class="k">return</span> <span class="nx">i</span><span class="p">(</span><span class="nx">o</span><span class="p">,</span><span class="o">!</span><span class="mi">0</span><span class="p">);</span><span class="k">throw</span> <span class="k">new</span> <span class="nb">Error</span><span class="p">(</span><span class="s2">&quot;Cannot find module &#39;&quot;</span><span class="o">+</span><span class="nx">o</span><span class="o">+</span><span class="s2">&quot;&#39;&quot;</span><span class="p">)}</span><span class="kd">var</span> <span class="nx">f</span><span class="o">=</span><span class="nx">n</span><span class="p">[</span><span class="nx">o</span><span class="p">]</span><span class="o">=</span><span class="p">{</span><span class="nx">exports</span><span class="o">:</span><span class="p">{}};</span><span class="nx">t</span><span class="p">[</span><span class="nx">o</span><span class="p">][</span><span class="mi">0</span><span class="p">].</span><span class="nx">call</span><span class="p">(</span><span class="nx">f</span><span class="p">.</span><span class="nx">exports</span><span class="p">,</span><span class="kd">function</span><span class="p">(</span><span class="nx">e</span><span class="p">){</span><span class="kd">var</span> <span class="nx">n</span><span class="o">=</span><span class="nx">t</span><span class="p">[</span><span class="nx">o</span><span class="p">][</span><span class="mi">1</span><span class="p">][</span><span class="nx">e</span><span class="p">];</span><span class="k">return</span> <span class="nx">s</span><span class="p">(</span><span class="nx">n</span><span class="o">?</span><span class="nx">n</span><span class="o">:</span><span class="nx">e</span><span class="p">)},</span><span class="nx">f</span><span class="p">,</span><span class="nx">f</span><span class="p">.</span><span class="nx">exports</span><span class="p">,</span><span class="nx">e</span><span class="p">,</span><span class="nx">t</span><span class="p">,</span><span class="nx">n</span><span class="p">,</span><span class="nx">r</span><span class="p">)}</span><span class="k">return</span> <span class="nx">n</span><span class="p">[</span><span class="nx">o</span><span class="p">].</span><span class="nx">exports</span><span class="p">}</span><span class="kd">var</span> <span class="nx">i</span><span class="o">=</span><span class="k">typeof</span> <span class="nx">require</span><span class="o">==</span><span class="s2">&quot;function&quot;</span><span class="o">&amp;&amp;</span><span class="nx">require</span><span class="p">;</span><span class="k">for</span><span class="p">(</span><span class="kd">var</span> <span class="nx">o</span><span class="o">=</span><span class="mi">0</span><span class="p">;</span><span class="nx">o</span><span class="o">&lt;</span><span class="nx">r</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span><span class="nx">o</span><span class="o">++</span><span class="p">)</span><span class="nx">s</span><span class="p">(</span><span class="nx">r</span><span class="p">[</span><span class="nx">o</span><span class="p">]);</span><span class="k">return</span> <span class="nx">s</span><span class="p">})({</span><span class="mi">1</span><span class="o">:</span><span class="p">[</span><span class="kd">function</span><span class="p">(</span><span class="nx">require</span><span class="p">,</span><span class="nx">module</span><span class="p">,</span><span class="nx">exports</span><span class="p">){</span></div><div class='line' id='LC2'><span class="cm">/**</span></div><div class='line' id='LC3'><span class="cm"> * 默认配置</span></div><div class='line' id='LC4'><span class="cm"> *</span></div><div class='line' id='LC5'><span class="cm"> * @author 老雷&lt;leizongmin@gmail.com&gt;</span></div><div class='line' id='LC6'><span class="cm"> */</span></div><div class='line' id='LC7'><br/></div><div class='line' id='LC8'><br/></div><div class='line' id='LC9'><span class="c1">// 默认白名单</span></div><div class='line' id='LC10'><span class="kd">var</span> <span class="nx">whiteList</span> <span class="o">=</span> <span class="p">{</span></div><div class='line' id='LC11'>&nbsp;&nbsp;<span class="nx">a</span><span class="o">:</span>      <span class="p">[</span><span class="s1">&#39;target&#39;</span><span class="p">,</span> <span class="s1">&#39;href&#39;</span><span class="p">,</span> <span class="s1">&#39;title&#39;</span><span class="p">],</span></div><div class='line' id='LC12'>&nbsp;&nbsp;<span class="nx">abbr</span><span class="o">:</span>   <span class="p">[</span><span class="s1">&#39;title&#39;</span><span class="p">],</span></div><div class='line' id='LC13'>&nbsp;&nbsp;<span class="nx">address</span><span class="o">:</span> <span class="p">[],</span></div><div class='line' id='LC14'>&nbsp;&nbsp;<span class="nx">area</span><span class="o">:</span>   <span class="p">[</span><span class="s1">&#39;shape&#39;</span><span class="p">,</span> <span class="s1">&#39;coords&#39;</span><span class="p">,</span> <span class="s1">&#39;href&#39;</span><span class="p">,</span> <span class="s1">&#39;alt&#39;</span><span class="p">],</span></div><div class='line' id='LC15'>&nbsp;&nbsp;<span class="nx">article</span><span class="o">:</span> <span class="p">[],</span></div><div class='line' id='LC16'>&nbsp;&nbsp;<span class="nx">aside</span><span class="o">:</span>  <span class="p">[],</span></div><div class='line' id='LC17'>&nbsp;&nbsp;<span class="nx">audio</span><span class="o">:</span>  <span class="p">[</span><span class="s1">&#39;autoplay&#39;</span><span class="p">,</span> <span class="s1">&#39;controls&#39;</span><span class="p">,</span> <span class="s1">&#39;loop&#39;</span><span class="p">,</span> <span class="s1">&#39;preload&#39;</span><span class="p">,</span> <span class="s1">&#39;src&#39;</span><span class="p">],</span></div><div class='line' id='LC18'>&nbsp;&nbsp;<span class="nx">b</span><span class="o">:</span>      <span class="p">[],</span></div><div class='line' id='LC19'>&nbsp;&nbsp;<span class="nx">bdi</span><span class="o">:</span>    <span class="p">[</span><span class="s1">&#39;dir&#39;</span><span class="p">],</span></div><div class='line' id='LC20'>&nbsp;&nbsp;<span class="nx">bdo</span><span class="o">:</span>    <span class="p">[</span><span class="s1">&#39;dir&#39;</span><span class="p">],</span></div><div class='line' id='LC21'>&nbsp;&nbsp;<span class="nx">big</span><span class="o">:</span>    <span class="p">[],</span></div><div class='line' id='LC22'>&nbsp;&nbsp;<span class="nx">blockquote</span><span class="o">:</span> <span class="p">[</span><span class="s1">&#39;cite&#39;</span><span class="p">],</span></div><div class='line' id='LC23'>&nbsp;&nbsp;<span class="nx">br</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC24'>&nbsp;&nbsp;<span class="nx">caption</span><span class="o">:</span> <span class="p">[],</span></div><div class='line' id='LC25'>&nbsp;&nbsp;<span class="nx">center</span><span class="o">:</span> <span class="p">[],</span></div><div class='line' id='LC26'>&nbsp;&nbsp;<span class="nx">cite</span><span class="o">:</span>   <span class="p">[],</span></div><div class='line' id='LC27'>&nbsp;&nbsp;<span class="nx">code</span><span class="o">:</span>   <span class="p">[],</span></div><div class='line' id='LC28'>&nbsp;&nbsp;<span class="nx">col</span><span class="o">:</span>    <span class="p">[</span><span class="s1">&#39;align&#39;</span><span class="p">,</span> <span class="s1">&#39;valign&#39;</span><span class="p">,</span> <span class="s1">&#39;span&#39;</span><span class="p">,</span> <span class="s1">&#39;width&#39;</span><span class="p">],</span></div><div class='line' id='LC29'>&nbsp;&nbsp;<span class="nx">colgroup</span><span class="o">:</span> <span class="p">[</span><span class="s1">&#39;align&#39;</span><span class="p">,</span> <span class="s1">&#39;valign&#39;</span><span class="p">,</span> <span class="s1">&#39;span&#39;</span><span class="p">,</span> <span class="s1">&#39;width&#39;</span><span class="p">],</span></div><div class='line' id='LC30'>&nbsp;&nbsp;<span class="nx">dd</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC31'>&nbsp;&nbsp;<span class="nx">del</span><span class="o">:</span>    <span class="p">[</span><span class="s1">&#39;datetime&#39;</span><span class="p">],</span></div><div class='line' id='LC32'>&nbsp;&nbsp;<span class="nx">details</span><span class="o">:</span> <span class="p">[</span><span class="s1">&#39;open&#39;</span><span class="p">],</span></div><div class='line' id='LC33'>&nbsp;&nbsp;<span class="nx">div</span><span class="o">:</span>    <span class="p">[],</span></div><div class='line' id='LC34'>&nbsp;&nbsp;<span class="nx">dl</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC35'>&nbsp;&nbsp;<span class="nx">dt</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC36'>&nbsp;&nbsp;<span class="nx">em</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC37'>&nbsp;&nbsp;<span class="nx">font</span><span class="o">:</span>   <span class="p">[</span><span class="s1">&#39;color&#39;</span><span class="p">,</span> <span class="s1">&#39;size&#39;</span><span class="p">,</span> <span class="s1">&#39;face&#39;</span><span class="p">],</span></div><div class='line' id='LC38'>&nbsp;&nbsp;<span class="nx">footer</span><span class="o">:</span> <span class="p">[],</span></div><div class='line' id='LC39'>&nbsp;&nbsp;<span class="nx">h1</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC40'>&nbsp;&nbsp;<span class="nx">h2</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC41'>&nbsp;&nbsp;<span class="nx">h3</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC42'>&nbsp;&nbsp;<span class="nx">h4</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC43'>&nbsp;&nbsp;<span class="nx">h5</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC44'>&nbsp;&nbsp;<span class="nx">h6</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC45'>&nbsp;&nbsp;<span class="nx">header</span><span class="o">:</span> <span class="p">[],</span></div><div class='line' id='LC46'>&nbsp;&nbsp;<span class="nx">hr</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC47'>&nbsp;&nbsp;<span class="nx">i</span><span class="o">:</span>      <span class="p">[],</span></div><div class='line' id='LC48'>&nbsp;&nbsp;<span class="nx">img</span><span class="o">:</span>    <span class="p">[</span><span class="s1">&#39;src&#39;</span><span class="p">,</span> <span class="s1">&#39;alt&#39;</span><span class="p">,</span> <span class="s1">&#39;title&#39;</span><span class="p">,</span> <span class="s1">&#39;width&#39;</span><span class="p">,</span> <span class="s1">&#39;height&#39;</span><span class="p">],</span></div><div class='line' id='LC49'>&nbsp;&nbsp;<span class="nx">ins</span><span class="o">:</span>    <span class="p">[</span><span class="s1">&#39;datetime&#39;</span><span class="p">],</span></div><div class='line' id='LC50'>&nbsp;&nbsp;<span class="nx">li</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC51'>&nbsp;&nbsp;<span class="nx">mark</span><span class="o">:</span>   <span class="p">[],</span></div><div class='line' id='LC52'>&nbsp;&nbsp;<span class="nx">nav</span><span class="o">:</span>    <span class="p">[],</span></div><div class='line' id='LC53'>&nbsp;&nbsp;<span class="nx">ol</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC54'>&nbsp;&nbsp;<span class="nx">p</span><span class="o">:</span>      <span class="p">[],</span></div><div class='line' id='LC55'>&nbsp;&nbsp;<span class="nx">pre</span><span class="o">:</span>    <span class="p">[],</span></div><div class='line' id='LC56'>&nbsp;&nbsp;<span class="nx">s</span><span class="o">:</span>      <span class="p">[],</span></div><div class='line' id='LC57'>&nbsp;&nbsp;<span class="nx">section</span><span class="o">:</span><span class="p">[],</span></div><div class='line' id='LC58'>&nbsp;&nbsp;<span class="nx">small</span><span class="o">:</span>  <span class="p">[],</span></div><div class='line' id='LC59'>&nbsp;&nbsp;<span class="nx">span</span><span class="o">:</span>   <span class="p">[],</span></div><div class='line' id='LC60'>&nbsp;&nbsp;<span class="nx">strong</span><span class="o">:</span> <span class="p">[],</span></div><div class='line' id='LC61'>&nbsp;&nbsp;<span class="nx">table</span><span class="o">:</span>  <span class="p">[</span><span class="s1">&#39;width&#39;</span><span class="p">,</span> <span class="s1">&#39;border&#39;</span><span class="p">,</span> <span class="s1">&#39;align&#39;</span><span class="p">,</span> <span class="s1">&#39;valign&#39;</span><span class="p">],</span></div><div class='line' id='LC62'>&nbsp;&nbsp;<span class="nx">tbody</span><span class="o">:</span>  <span class="p">[</span><span class="s1">&#39;align&#39;</span><span class="p">,</span> <span class="s1">&#39;valign&#39;</span><span class="p">],</span></div><div class='line' id='LC63'>&nbsp;&nbsp;<span class="nx">td</span><span class="o">:</span>     <span class="p">[</span><span class="s1">&#39;width&#39;</span><span class="p">,</span> <span class="s1">&#39;colspan&#39;</span><span class="p">,</span> <span class="s1">&#39;align&#39;</span><span class="p">,</span> <span class="s1">&#39;valign&#39;</span><span class="p">],</span></div><div class='line' id='LC64'>&nbsp;&nbsp;<span class="nx">tfoot</span><span class="o">:</span>  <span class="p">[</span><span class="s1">&#39;align&#39;</span><span class="p">,</span> <span class="s1">&#39;valign&#39;</span><span class="p">],</span></div><div class='line' id='LC65'>&nbsp;&nbsp;<span class="nx">th</span><span class="o">:</span>     <span class="p">[</span><span class="s1">&#39;width&#39;</span><span class="p">,</span> <span class="s1">&#39;colspan&#39;</span><span class="p">,</span> <span class="s1">&#39;align&#39;</span><span class="p">,</span> <span class="s1">&#39;valign&#39;</span><span class="p">],</span></div><div class='line' id='LC66'>&nbsp;&nbsp;<span class="nx">thead</span><span class="o">:</span>  <span class="p">[</span><span class="s1">&#39;align&#39;</span><span class="p">,</span> <span class="s1">&#39;valign&#39;</span><span class="p">],</span></div><div class='line' id='LC67'>&nbsp;&nbsp;<span class="nx">tr</span><span class="o">:</span>     <span class="p">[</span><span class="s1">&#39;rowspan&#39;</span><span class="p">,</span> <span class="s1">&#39;align&#39;</span><span class="p">,</span> <span class="s1">&#39;valign&#39;</span><span class="p">],</span></div><div class='line' id='LC68'>&nbsp;&nbsp;<span class="nx">tt</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC69'>&nbsp;&nbsp;<span class="nx">u</span><span class="o">:</span>      <span class="p">[],</span></div><div class='line' id='LC70'>&nbsp;&nbsp;<span class="nx">ul</span><span class="o">:</span>     <span class="p">[],</span></div><div class='line' id='LC71'>&nbsp;&nbsp;<span class="nx">video</span><span class="o">:</span>  <span class="p">[</span><span class="s1">&#39;autoplay&#39;</span><span class="p">,</span> <span class="s1">&#39;controls&#39;</span><span class="p">,</span> <span class="s1">&#39;loop&#39;</span><span class="p">,</span> <span class="s1">&#39;preload&#39;</span><span class="p">,</span> <span class="s1">&#39;src&#39;</span><span class="p">,</span> <span class="s1">&#39;height&#39;</span><span class="p">,</span> <span class="s1">&#39;width&#39;</span><span class="p">]</span></div><div class='line' id='LC72'><span class="p">};</span></div><div class='line' id='LC73'><br/></div><div class='line' id='LC74'><span class="cm">/**</span></div><div class='line' id='LC75'><span class="cm"> * 匹配到标签时的处理方法</span></div><div class='line' id='LC76'><span class="cm"> *</span></div><div class='line' id='LC77'><span class="cm"> * @param {String} tag</span></div><div class='line' id='LC78'><span class="cm"> * @param {String} html</span></div><div class='line' id='LC79'><span class="cm"> * @param {Object} options</span></div><div class='line' id='LC80'><span class="cm"> * @return {String}</span></div><div class='line' id='LC81'><span class="cm"> */</span></div><div class='line' id='LC82'><span class="kd">function</span> <span class="nx">onTag</span> <span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">html</span><span class="p">,</span> <span class="nx">options</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC83'>&nbsp;&nbsp;<span class="c1">// do nothing</span></div><div class='line' id='LC84'><span class="p">}</span></div><div class='line' id='LC85'><br/></div><div class='line' id='LC86'><span class="cm">/**</span></div><div class='line' id='LC87'><span class="cm"> * 匹配到不在白名单上的标签时的处理方法</span></div><div class='line' id='LC88'><span class="cm"> *</span></div><div class='line' id='LC89'><span class="cm"> * @param {String} tag</span></div><div class='line' id='LC90'><span class="cm"> * @param {String} html</span></div><div class='line' id='LC91'><span class="cm"> * @param {Object} options</span></div><div class='line' id='LC92'><span class="cm"> * @return {String}</span></div><div class='line' id='LC93'><span class="cm"> */</span></div><div class='line' id='LC94'><span class="kd">function</span> <span class="nx">onIgnoreTag</span> <span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">html</span><span class="p">,</span> <span class="nx">options</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC95'>&nbsp;&nbsp;<span class="c1">// do nothing</span></div><div class='line' id='LC96'><span class="p">}</span></div><div class='line' id='LC97'><br/></div><div class='line' id='LC98'><span class="cm">/**</span></div><div class='line' id='LC99'><span class="cm"> * 匹配到标签属性时的处理方法</span></div><div class='line' id='LC100'><span class="cm"> *</span></div><div class='line' id='LC101'><span class="cm"> * @param {String} tag</span></div><div class='line' id='LC102'><span class="cm"> * @param {String} name</span></div><div class='line' id='LC103'><span class="cm"> * @param {String} value</span></div><div class='line' id='LC104'><span class="cm"> * @return {String}</span></div><div class='line' id='LC105'><span class="cm"> */</span></div><div class='line' id='LC106'><span class="kd">function</span> <span class="nx">onTagAttr</span> <span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">name</span><span class="p">,</span> <span class="nx">value</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC107'>&nbsp;&nbsp;<span class="c1">// do nothing</span></div><div class='line' id='LC108'><span class="p">}</span></div><div class='line' id='LC109'><br/></div><div class='line' id='LC110'><span class="cm">/**</span></div><div class='line' id='LC111'><span class="cm"> * 匹配到不在白名单上的标签属性时的处理方法</span></div><div class='line' id='LC112'><span class="cm"> *</span></div><div class='line' id='LC113'><span class="cm"> * @param {String} tag</span></div><div class='line' id='LC114'><span class="cm"> * @param {String} name</span></div><div class='line' id='LC115'><span class="cm"> * @param {String} value</span></div><div class='line' id='LC116'><span class="cm"> * @return {String}</span></div><div class='line' id='LC117'><span class="cm"> */</span></div><div class='line' id='LC118'><span class="kd">function</span> <span class="nx">onIgnoreTagAttr</span> <span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">name</span><span class="p">,</span> <span class="nx">value</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC119'>&nbsp;&nbsp;<span class="c1">// do nothing</span></div><div class='line' id='LC120'><span class="p">}</span></div><div class='line' id='LC121'><br/></div><div class='line' id='LC122'><span class="cm">/**</span></div><div class='line' id='LC123'><span class="cm"> * HTML转义</span></div><div class='line' id='LC124'><span class="cm"> *</span></div><div class='line' id='LC125'><span class="cm"> * @param {String} html</span></div><div class='line' id='LC126'><span class="cm"> */</span></div><div class='line' id='LC127'><span class="kd">function</span> <span class="nx">escapeHtml</span> <span class="p">(</span><span class="nx">html</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC128'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">html</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="nx">REGEXP_LT</span><span class="p">,</span> <span class="s1">&#39;&amp;lt;&#39;</span><span class="p">).</span><span class="nx">replace</span><span class="p">(</span><span class="nx">REGEXP_GT</span><span class="p">,</span> <span class="s1">&#39;&amp;gt;&#39;</span><span class="p">);</span></div><div class='line' id='LC129'><span class="p">}</span></div><div class='line' id='LC130'><br/></div><div class='line' id='LC131'><span class="cm">/**</span></div><div class='line' id='LC132'><span class="cm"> * 安全的标签属性值</span></div><div class='line' id='LC133'><span class="cm"> *</span></div><div class='line' id='LC134'><span class="cm"> * @param {String} tag</span></div><div class='line' id='LC135'><span class="cm"> * @param {String} name</span></div><div class='line' id='LC136'><span class="cm"> * @param {String} value</span></div><div class='line' id='LC137'><span class="cm"> * @return {String}</span></div><div class='line' id='LC138'><span class="cm"> */</span></div><div class='line' id='LC139'><span class="kd">function</span> <span class="nx">safeAttrValue</span> <span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">name</span><span class="p">,</span> <span class="nx">value</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC140'>&nbsp;&nbsp;<span class="c1">// 转换为友好的属性值，再做判断</span></div><div class='line' id='LC141'>&nbsp;&nbsp;<span class="nx">value</span> <span class="o">=</span> <span class="nx">friendlyAttrValue</span><span class="p">(</span><span class="nx">value</span><span class="p">);</span></div><div class='line' id='LC142'><br/></div><div class='line' id='LC143'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">name</span> <span class="o">===</span> <span class="s1">&#39;href&#39;</span> <span class="o">||</span> <span class="nx">name</span> <span class="o">===</span> <span class="s1">&#39;src&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC144'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 过滤 href 和 src 属性</span></div><div class='line' id='LC145'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 仅允许 http:// | https:// | / 开头的地址</span></div><div class='line' id='LC146'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">value</span> <span class="o">=</span> <span class="nx">value</span><span class="p">.</span><span class="nx">trim</span><span class="p">();</span></div><div class='line' id='LC147'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">value</span> <span class="o">===</span> <span class="s1">&#39;#&#39;</span><span class="p">)</span> <span class="k">return</span> <span class="s1">&#39;#&#39;</span><span class="p">;</span></div><div class='line' id='LC148'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">value</span> <span class="o">&amp;&amp;</span> <span class="o">!</span><span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_1</span><span class="p">.</span><span class="nx">test</span><span class="p">(</span><span class="nx">value</span><span class="p">))</span> <span class="p">{</span></div><div class='line' id='LC149'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="s1">&#39;&#39;</span><span class="p">;</span></div><div class='line' id='LC150'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC151'>&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="k">if</span> <span class="p">(</span><span class="nx">name</span> <span class="o">===</span> <span class="s1">&#39;background&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC152'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 过滤 background 属性 （这个xss漏洞较老了，可能已经不适用）</span></div><div class='line' id='LC153'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// javascript:</span></div><div class='line' id='LC154'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_4</span><span class="p">.</span><span class="nx">lastIndex</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span></div><div class='line' id='LC155'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_4</span><span class="p">.</span><span class="nx">test</span><span class="p">(</span><span class="nx">value</span><span class="p">))</span> <span class="p">{</span></div><div class='line' id='LC156'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="s1">&#39;&#39;</span><span class="p">;</span></div><div class='line' id='LC157'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC158'>&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="k">if</span> <span class="p">(</span><span class="nx">name</span> <span class="o">===</span> <span class="s1">&#39;style&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC159'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// /*注释*/</span></div><div class='line' id='LC160'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_3</span><span class="p">.</span><span class="nx">lastIndex</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span></div><div class='line' id='LC161'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_3</span><span class="p">.</span><span class="nx">test</span><span class="p">(</span><span class="nx">value</span><span class="p">))</span> <span class="p">{</span></div><div class='line' id='LC162'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="s1">&#39;&#39;</span><span class="p">;</span></div><div class='line' id='LC163'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC164'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// expression()</span></div><div class='line' id='LC165'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_7</span><span class="p">.</span><span class="nx">lastIndex</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span></div><div class='line' id='LC166'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_7</span><span class="p">.</span><span class="nx">test</span><span class="p">(</span><span class="nx">value</span><span class="p">))</span> <span class="p">{</span></div><div class='line' id='LC167'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="s1">&#39;&#39;</span><span class="p">;</span></div><div class='line' id='LC168'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC169'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// url()</span></div><div class='line' id='LC170'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_8</span><span class="p">.</span><span class="nx">lastIndex</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span></div><div class='line' id='LC171'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_8</span><span class="p">.</span><span class="nx">test</span><span class="p">(</span><span class="nx">value</span><span class="p">))</span> <span class="p">{</span></div><div class='line' id='LC172'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_4</span><span class="p">.</span><span class="nx">lastIndex</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span></div><div class='line' id='LC173'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_4</span><span class="p">.</span><span class="nx">test</span><span class="p">(</span><span class="nx">value</span><span class="p">))</span> <span class="p">{</span></div><div class='line' id='LC174'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="s1">&#39;&#39;</span><span class="p">;</span></div><div class='line' id='LC175'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC176'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC177'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC178'><br/></div><div class='line' id='LC179'>&nbsp;&nbsp;<span class="c1">// 输出时需要转义&lt;&gt;&quot;</span></div><div class='line' id='LC180'>&nbsp;&nbsp;<span class="nx">value</span> <span class="o">=</span> <span class="nx">escapeAttrValue</span><span class="p">(</span><span class="nx">value</span><span class="p">);</span></div><div class='line' id='LC181'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">value</span><span class="p">;</span></div><div class='line' id='LC182'><span class="p">}</span></div><div class='line' id='LC183'><br/></div><div class='line' id='LC184'><span class="c1">// 正则表达式</span></div><div class='line' id='LC185'><span class="kd">var</span> <span class="nx">REGEXP_LT</span> <span class="o">=</span> <span class="sr">/&lt;/g</span><span class="p">;</span></div><div class='line' id='LC186'><span class="kd">var</span> <span class="nx">REGEXP_GT</span> <span class="o">=</span> <span class="sr">/&gt;/g</span><span class="p">;</span></div><div class='line' id='LC187'><span class="kd">var</span> <span class="nx">REGEXP_QUOTE</span> <span class="o">=</span> <span class="sr">/&quot;/g</span><span class="p">;</span></div><div class='line' id='LC188'><span class="kd">var</span> <span class="nx">REGEXP_QUOTE_2</span> <span class="o">=</span> <span class="sr">/&amp;quot;/g</span><span class="p">;</span></div><div class='line' id='LC189'><span class="kd">var</span> <span class="nx">REGEXP_ATTR_VALUE_1</span> <span class="o">=</span> <span class="sr">/&amp;#([a-zA-Z0-9]*);?/img</span><span class="p">;</span></div><div class='line' id='LC190'><span class="kd">var</span> <span class="nx">REGEXP_ATTR_VALUE_COLON</span> <span class="o">=</span> <span class="sr">/&amp;colon;?/img</span><span class="p">;</span></div><div class='line' id='LC191'><span class="kd">var</span> <span class="nx">REGEXP_ATTR_VALUE_NEWLINE</span> <span class="o">=</span> <span class="sr">/&amp;newline;?/img</span><span class="p">;</span></div><div class='line' id='LC192'><span class="kd">var</span> <span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_1</span> <span class="o">=</span> <span class="sr">/^((https?:\/)?\/)/</span><span class="p">;</span></div><div class='line' id='LC193'><span class="kd">var</span> <span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_3</span> <span class="o">=</span> <span class="sr">/\/\*|\*\//mg</span><span class="p">;</span></div><div class='line' id='LC194'><span class="kd">var</span> <span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_4</span> <span class="o">=</span> <span class="sr">/((j\s*a\s*v\s*a|v\s*b|l\s*i\s*v\s*e)\s*s\s*c\s*r\s*i\s*p\s*t\s*|m\s*o\s*c\s*h\s*a)\:/ig</span><span class="p">;</span></div><div class='line' id='LC195'><span class="kd">var</span> <span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_5</span> <span class="o">=</span> <span class="sr">/^[\s&quot;&#39;`]*(d\s*a\s*t\s*a\s*)\:/ig</span><span class="p">;</span></div><div class='line' id='LC196'><span class="kd">var</span> <span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_6</span> <span class="o">=</span> <span class="sr">/^[\s&quot;&#39;`]*(d\s*a\s*t\s*a\s*)\:\s*image\//ig</span><span class="p">;</span></div><div class='line' id='LC197'><span class="kd">var</span> <span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_7</span> <span class="o">=</span> <span class="sr">/e\s*x\s*p\s*r\s*e\s*s\s*s\s*i\s*o\s*n\s*\(.*/ig</span><span class="p">;</span></div><div class='line' id='LC198'><span class="kd">var</span> <span class="nx">REGEXP_DEFAULT_ON_TAG_ATTR_8</span> <span class="o">=</span> <span class="sr">/u\s*r\s*l\s*\(.*/ig</span><span class="p">;</span></div><div class='line' id='LC199'><br/></div><div class='line' id='LC200'><span class="cm">/**</span></div><div class='line' id='LC201'><span class="cm"> * 对双引号进行转义</span></div><div class='line' id='LC202'><span class="cm"> *</span></div><div class='line' id='LC203'><span class="cm"> * @param {String} str</span></div><div class='line' id='LC204'><span class="cm"> * @return {String} str</span></div><div class='line' id='LC205'><span class="cm"> */</span></div><div class='line' id='LC206'><span class="kd">function</span> <span class="nx">escapeQuote</span> <span class="p">(</span><span class="nx">str</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC207'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">str</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="nx">REGEXP_QUOTE</span><span class="p">,</span> <span class="s1">&#39;&amp;quote;&#39;</span><span class="p">);</span></div><div class='line' id='LC208'><span class="p">}</span></div><div class='line' id='LC209'><br/></div><div class='line' id='LC210'><span class="cm">/**</span></div><div class='line' id='LC211'><span class="cm"> * 对双引号进行转义</span></div><div class='line' id='LC212'><span class="cm"> *</span></div><div class='line' id='LC213'><span class="cm"> * @param {String} str</span></div><div class='line' id='LC214'><span class="cm"> * @return {String} str</span></div><div class='line' id='LC215'><span class="cm"> */</span></div><div class='line' id='LC216'><span class="kd">function</span> <span class="nx">unescapeQuote</span> <span class="p">(</span><span class="nx">str</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC217'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">str</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="nx">REGEXP_QUOTE_2</span><span class="p">,</span> <span class="s1">&#39;&quot;&#39;</span><span class="p">);</span></div><div class='line' id='LC218'><span class="p">}</span></div><div class='line' id='LC219'><br/></div><div class='line' id='LC220'><span class="cm">/**</span></div><div class='line' id='LC221'><span class="cm"> * 对html实体编码进行转义</span></div><div class='line' id='LC222'><span class="cm"> *</span></div><div class='line' id='LC223'><span class="cm"> * @param {String} str</span></div><div class='line' id='LC224'><span class="cm"> * @return {String}</span></div><div class='line' id='LC225'><span class="cm"> */</span></div><div class='line' id='LC226'><span class="kd">function</span> <span class="nx">escapeHtmlEntities</span> <span class="p">(</span><span class="nx">str</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC227'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">str</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="nx">REGEXP_ATTR_VALUE_1</span><span class="p">,</span> <span class="kd">function</span> <span class="nx">replaceUnicode</span> <span class="p">(</span><span class="nx">str</span><span class="p">,</span> <span class="nx">code</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC228'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="p">(</span><span class="nx">code</span><span class="p">[</span><span class="mi">0</span><span class="p">]</span> <span class="o">===</span> <span class="s1">&#39;x&#39;</span> <span class="o">||</span> <span class="nx">code</span><span class="p">[</span><span class="mi">0</span><span class="p">]</span> <span class="o">===</span> <span class="s1">&#39;X&#39;</span><span class="p">)</span></div><div class='line' id='LC229'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="o">?</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(</span><span class="nb">parseInt</span><span class="p">(</span><span class="nx">code</span><span class="p">.</span><span class="nx">substr</span><span class="p">(</span><span class="mi">1</span><span class="p">),</span> <span class="mi">16</span><span class="p">))</span></div><div class='line' id='LC230'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="o">:</span> <span class="nb">String</span><span class="p">.</span><span class="nx">fromCharCode</span><span class="p">(</span><span class="nb">parseInt</span><span class="p">(</span><span class="nx">code</span><span class="p">,</span> <span class="mi">10</span><span class="p">));</span></div><div class='line' id='LC231'>&nbsp;&nbsp;<span class="p">});</span></div><div class='line' id='LC232'><span class="p">}</span></div><div class='line' id='LC233'><br/></div><div class='line' id='LC234'><span class="cm">/**</span></div><div class='line' id='LC235'><span class="cm"> * 对html5新增的危险实体编码进行转义</span></div><div class='line' id='LC236'><span class="cm"> *</span></div><div class='line' id='LC237'><span class="cm"> * @param {String} str</span></div><div class='line' id='LC238'><span class="cm"> * @return {String}</span></div><div class='line' id='LC239'><span class="cm"> */</span></div><div class='line' id='LC240'><span class="kd">function</span> <span class="nx">escapeDangerHtml5Entities</span> <span class="p">(</span><span class="nx">str</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC241'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">str</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="nx">REGEXP_ATTR_VALUE_COLON</span><span class="p">,</span> <span class="s1">&#39;:&#39;</span><span class="p">)</span></div><div class='line' id='LC242'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="nx">REGEXP_ATTR_VALUE_NEWLINE</span><span class="p">,</span> <span class="s1">&#39; &#39;</span><span class="p">);</span></div><div class='line' id='LC243'><span class="p">}</span></div><div class='line' id='LC244'><br/></div><div class='line' id='LC245'><span class="cm">/**</span></div><div class='line' id='LC246'><span class="cm"> * 清除不可见字符</span></div><div class='line' id='LC247'><span class="cm"> *</span></div><div class='line' id='LC248'><span class="cm"> * @param {String} str</span></div><div class='line' id='LC249'><span class="cm"> * @return {String}</span></div><div class='line' id='LC250'><span class="cm"> */</span></div><div class='line' id='LC251'><span class="kd">function</span> <span class="nx">clearNonPrintableCharacter</span> <span class="p">(</span><span class="nx">str</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC252'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">str2</span> <span class="o">=</span> <span class="s1">&#39;&#39;</span><span class="p">;</span></div><div class='line' id='LC253'>&nbsp;&nbsp;<span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">,</span> <span class="nx">len</span> <span class="o">=</span> <span class="nx">str</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span> <span class="nx">i</span> <span class="o">&lt;</span> <span class="nx">len</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC254'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">str2</span> <span class="o">+=</span> <span class="nx">str</span><span class="p">.</span><span class="nx">charCodeAt</span><span class="p">(</span><span class="nx">i</span><span class="p">)</span> <span class="o">&lt;</span> <span class="mi">32</span> <span class="o">?</span> <span class="s1">&#39; &#39;</span> <span class="o">:</span> <span class="nx">str</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">i</span><span class="p">);</span></div><div class='line' id='LC255'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC256'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">str2</span><span class="p">.</span><span class="nx">trim</span><span class="p">();</span></div><div class='line' id='LC257'><span class="p">}</span></div><div class='line' id='LC258'><br/></div><div class='line' id='LC259'><span class="cm">/**</span></div><div class='line' id='LC260'><span class="cm"> * 将标签的属性值转换成一般字符，便于分析</span></div><div class='line' id='LC261'><span class="cm"> *</span></div><div class='line' id='LC262'><span class="cm"> * @param {String} str</span></div><div class='line' id='LC263'><span class="cm"> * @return {String}</span></div><div class='line' id='LC264'><span class="cm"> */</span></div><div class='line' id='LC265'><span class="kd">function</span> <span class="nx">friendlyAttrValue</span> <span class="p">(</span><span class="nx">str</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC266'>&nbsp;&nbsp;<span class="nx">str</span> <span class="o">=</span> <span class="nx">unescapeQuote</span><span class="p">(</span><span class="nx">str</span><span class="p">);</span>             <span class="c1">// 双引号</span></div><div class='line' id='LC267'>&nbsp;&nbsp;<span class="nx">str</span> <span class="o">=</span> <span class="nx">escapeHtmlEntities</span><span class="p">(</span><span class="nx">str</span><span class="p">);</span>         <span class="c1">// 转换HTML实体编码</span></div><div class='line' id='LC268'>&nbsp;&nbsp;<span class="nx">str</span> <span class="o">=</span> <span class="nx">escapeDangerHtml5Entities</span><span class="p">(</span><span class="nx">str</span><span class="p">);</span>  <span class="c1">// 转换危险的HTML5新增实体编码</span></div><div class='line' id='LC269'>&nbsp;&nbsp;<span class="nx">str</span> <span class="o">=</span> <span class="nx">clearNonPrintableCharacter</span><span class="p">(</span><span class="nx">str</span><span class="p">);</span> <span class="c1">// 清除不可见字符</span></div><div class='line' id='LC270'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">str</span><span class="p">;</span></div><div class='line' id='LC271'><span class="p">}</span></div><div class='line' id='LC272'><br/></div><div class='line' id='LC273'><span class="cm">/**</span></div><div class='line' id='LC274'><span class="cm"> * 转义用于输出的标签属性值</span></div><div class='line' id='LC275'><span class="cm"> *</span></div><div class='line' id='LC276'><span class="cm"> * @param {String} str</span></div><div class='line' id='LC277'><span class="cm"> * @return {String}</span></div><div class='line' id='LC278'><span class="cm"> */</span></div><div class='line' id='LC279'><span class="kd">function</span> <span class="nx">escapeAttrValue</span> <span class="p">(</span><span class="nx">str</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC280'>&nbsp;&nbsp;<span class="nx">str</span> <span class="o">=</span> <span class="nx">escapeQuote</span><span class="p">(</span><span class="nx">str</span><span class="p">);</span></div><div class='line' id='LC281'>&nbsp;&nbsp;<span class="nx">str</span> <span class="o">=</span> <span class="nx">escapeHtml</span><span class="p">(</span><span class="nx">str</span><span class="p">);</span></div><div class='line' id='LC282'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">str</span><span class="p">;</span></div><div class='line' id='LC283'><span class="p">}</span></div><div class='line' id='LC284'><br/></div><div class='line' id='LC285'><span class="cm">/**</span></div><div class='line' id='LC286'><span class="cm"> * 去掉不在白名单中的标签onIgnoreTag处理方法</span></div><div class='line' id='LC287'><span class="cm"> */</span></div><div class='line' id='LC288'><span class="kd">function</span> <span class="nx">onIgnoreTagStripAll</span> <span class="p">()</span> <span class="p">{</span></div><div class='line' id='LC289'>&nbsp;&nbsp;<span class="k">return</span> <span class="s1">&#39;&#39;</span><span class="p">;</span></div><div class='line' id='LC290'><span class="p">}</span></div><div class='line' id='LC291'><br/></div><div class='line' id='LC292'><span class="cm">/**</span></div><div class='line' id='LC293'><span class="cm"> * 删除标签体</span></div><div class='line' id='LC294'><span class="cm"> *</span></div><div class='line' id='LC295'><span class="cm"> * @param {array} tags 要删除的标签列表</span></div><div class='line' id='LC296'><span class="cm"> * @param {function} next 对不在列表中的标签的处理函数，可选</span></div><div class='line' id='LC297'><span class="cm"> */</span></div><div class='line' id='LC298'><span class="kd">function</span> <span class="nx">StripTagBody</span> <span class="p">(</span><span class="nx">tags</span><span class="p">,</span> <span class="nx">next</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC299'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="k">typeof</span><span class="p">(</span><span class="nx">next</span><span class="p">)</span> <span class="o">!==</span> <span class="s1">&#39;function&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC300'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">next</span> <span class="o">=</span> <span class="kd">function</span> <span class="p">()</span> <span class="p">{};</span></div><div class='line' id='LC301'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC302'><br/></div><div class='line' id='LC303'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">isRemoveAllTag</span> <span class="o">=</span> <span class="o">!</span><span class="nb">Array</span><span class="p">.</span><span class="nx">isArray</span><span class="p">(</span><span class="nx">tags</span><span class="p">);</span></div><div class='line' id='LC304'>&nbsp;&nbsp;<span class="kd">function</span> <span class="nx">isRemoveTag</span> <span class="p">(</span><span class="nx">tag</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC305'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">isRemoveAllTag</span><span class="p">)</span> <span class="k">return</span> <span class="kc">true</span><span class="p">;</span></div><div class='line' id='LC306'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="p">(</span><span class="nx">tags</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="nx">tag</span><span class="p">)</span> <span class="o">!==</span> <span class="o">-</span><span class="mi">1</span><span class="p">);</span></div><div class='line' id='LC307'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC308'><br/></div><div class='line' id='LC309'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">removeList</span> <span class="o">=</span> <span class="p">[];</span>   <span class="c1">// 要删除的位置范围列表</span></div><div class='line' id='LC310'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">posStart</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span>  <span class="c1">// 当前标签开始位置</span></div><div class='line' id='LC311'><br/></div><div class='line' id='LC312'>&nbsp;&nbsp;<span class="k">return</span> <span class="p">{</span></div><div class='line' id='LC313'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">onIgnoreTag</span><span class="o">:</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">html</span><span class="p">,</span> <span class="nx">options</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC314'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">isRemoveTag</span><span class="p">(</span><span class="nx">tag</span><span class="p">))</span> <span class="p">{</span></div><div class='line' id='LC315'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">options</span><span class="p">.</span><span class="nx">isClosing</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC316'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">ret</span> <span class="o">=</span> <span class="s1">&#39;[/removed]&#39;</span><span class="p">;</span></div><div class='line' id='LC317'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">end</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">position</span> <span class="o">+</span> <span class="nx">ret</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span></div><div class='line' id='LC318'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">removeList</span><span class="p">.</span><span class="nx">push</span><span class="p">([</span><span class="nx">posStart</span> <span class="o">!==</span> <span class="kc">false</span> <span class="o">?</span> <span class="nx">posStart</span> <span class="o">:</span> <span class="nx">options</span><span class="p">.</span><span class="nx">position</span><span class="p">,</span> <span class="nx">end</span><span class="p">]);</span></div><div class='line' id='LC319'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">posStart</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span></div><div class='line' id='LC320'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">ret</span><span class="p">;</span></div><div class='line' id='LC321'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC322'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">posStart</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC323'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">posStart</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">position</span><span class="p">;</span></div><div class='line' id='LC324'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC325'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="s1">&#39;[removed]&#39;</span><span class="p">;</span></div><div class='line' id='LC326'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC327'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC328'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">next</span><span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">html</span><span class="p">,</span> <span class="nx">options</span><span class="p">);</span></div><div class='line' id='LC329'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC330'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">},</span></div><div class='line' id='LC331'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">remove</span><span class="o">:</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">html</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC332'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">rethtml</span> <span class="o">=</span> <span class="s1">&#39;&#39;</span><span class="p">;</span></div><div class='line' id='LC333'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">lastPos</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span></div><div class='line' id='LC334'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">removeList</span><span class="p">.</span><span class="nx">forEach</span><span class="p">(</span><span class="kd">function</span> <span class="p">(</span><span class="nx">pos</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC335'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">rethtml</span> <span class="o">+=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">lastPos</span><span class="p">,</span> <span class="nx">pos</span><span class="p">[</span><span class="mi">0</span><span class="p">]);</span></div><div class='line' id='LC336'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">lastPos</span> <span class="o">=</span> <span class="nx">pos</span><span class="p">[</span><span class="mi">1</span><span class="p">];</span></div><div class='line' id='LC337'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">});</span></div><div class='line' id='LC338'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">rethtml</span> <span class="o">+=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">lastPos</span><span class="p">);</span></div><div class='line' id='LC339'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">rethtml</span><span class="p">;</span></div><div class='line' id='LC340'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC341'>&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC342'><span class="p">}</span></div><div class='line' id='LC343'><br/></div><div class='line' id='LC344'><span class="cm">/**</span></div><div class='line' id='LC345'><span class="cm"> * 去除备注标签</span></div><div class='line' id='LC346'><span class="cm"> *</span></div><div class='line' id='LC347'><span class="cm"> * @param {String} html</span></div><div class='line' id='LC348'><span class="cm"> * @return {String}</span></div><div class='line' id='LC349'><span class="cm"> */</span></div><div class='line' id='LC350'><span class="kd">function</span> <span class="nx">stripCommentTag</span> <span class="p">(</span><span class="nx">html</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC351'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">html</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="nx">STRIP_COMMENT_TAG_REGEXP</span><span class="p">,</span> <span class="s1">&#39;&#39;</span><span class="p">);</span></div><div class='line' id='LC352'><span class="p">}</span></div><div class='line' id='LC353'><span class="kd">var</span> <span class="nx">STRIP_COMMENT_TAG_REGEXP</span> <span class="o">=</span> <span class="sr">/&lt;!--(.|\s)*?--&gt;/gm</span><span class="p">;</span></div><div class='line' id='LC354'><br/></div><div class='line' id='LC355'><br/></div><div class='line' id='LC356'><span class="nx">exports</span><span class="p">.</span><span class="nx">whiteList</span> <span class="o">=</span> <span class="nx">whiteList</span><span class="p">;</span></div><div class='line' id='LC357'><span class="nx">exports</span><span class="p">.</span><span class="nx">onTag</span> <span class="o">=</span> <span class="nx">onTag</span><span class="p">;</span></div><div class='line' id='LC358'><span class="nx">exports</span><span class="p">.</span><span class="nx">onIgnoreTag</span> <span class="o">=</span> <span class="nx">onIgnoreTag</span><span class="p">;</span></div><div class='line' id='LC359'><span class="nx">exports</span><span class="p">.</span><span class="nx">onTagAttr</span> <span class="o">=</span> <span class="nx">onTagAttr</span><span class="p">;</span></div><div class='line' id='LC360'><span class="nx">exports</span><span class="p">.</span><span class="nx">onIgnoreTagAttr</span> <span class="o">=</span> <span class="nx">onIgnoreTagAttr</span><span class="p">;</span></div><div class='line' id='LC361'><span class="nx">exports</span><span class="p">.</span><span class="nx">safeAttrValue</span> <span class="o">=</span> <span class="nx">safeAttrValue</span><span class="p">;</span></div><div class='line' id='LC362'><span class="nx">exports</span><span class="p">.</span><span class="nx">escapeHtml</span> <span class="o">=</span> <span class="nx">escapeHtml</span><span class="p">;</span></div><div class='line' id='LC363'><span class="nx">exports</span><span class="p">.</span><span class="nx">escapeQuote</span> <span class="o">=</span> <span class="nx">escapeQuote</span><span class="p">;</span></div><div class='line' id='LC364'><span class="nx">exports</span><span class="p">.</span><span class="nx">unescapeQuote</span> <span class="o">=</span> <span class="nx">unescapeQuote</span><span class="p">;</span></div><div class='line' id='LC365'><span class="nx">exports</span><span class="p">.</span><span class="nx">escapeHtmlEntities</span> <span class="o">=</span> <span class="nx">escapeHtmlEntities</span><span class="p">;</span></div><div class='line' id='LC366'><span class="nx">exports</span><span class="p">.</span><span class="nx">escapeDangerHtml5Entities</span> <span class="o">=</span> <span class="nx">escapeDangerHtml5Entities</span><span class="p">;</span></div><div class='line' id='LC367'><span class="nx">exports</span><span class="p">.</span><span class="nx">clearNonPrintableCharacter</span> <span class="o">=</span> <span class="nx">clearNonPrintableCharacter</span><span class="p">;</span></div><div class='line' id='LC368'><span class="nx">exports</span><span class="p">.</span><span class="nx">friendlyAttrValue</span> <span class="o">=</span> <span class="nx">friendlyAttrValue</span><span class="p">;</span></div><div class='line' id='LC369'><span class="nx">exports</span><span class="p">.</span><span class="nx">escapeAttrValue</span> <span class="o">=</span> <span class="nx">escapeAttrValue</span><span class="p">;</span></div><div class='line' id='LC370'><span class="nx">exports</span><span class="p">.</span><span class="nx">onIgnoreTagStripAll</span> <span class="o">=</span> <span class="nx">onIgnoreTagStripAll</span><span class="p">;</span></div><div class='line' id='LC371'><span class="nx">exports</span><span class="p">.</span><span class="nx">StripTagBody</span> <span class="o">=</span> <span class="nx">StripTagBody</span><span class="p">;</span></div><div class='line' id='LC372'><span class="nx">exports</span><span class="p">.</span><span class="nx">stripCommentTag</span> <span class="o">=</span> <span class="nx">stripCommentTag</span><span class="p">;</span></div><div class='line' id='LC373'><br/></div><div class='line' id='LC374'><span class="p">},{}],</span><span class="mi">2</span><span class="o">:</span><span class="p">[</span><span class="kd">function</span><span class="p">(</span><span class="nx">require</span><span class="p">,</span><span class="nx">module</span><span class="p">,</span><span class="nx">exports</span><span class="p">){</span></div><div class='line' id='LC375'><span class="cm">/**</span></div><div class='line' id='LC376'><span class="cm"> * 模块入口</span></div><div class='line' id='LC377'><span class="cm"> *</span></div><div class='line' id='LC378'><span class="cm"> * @author 老雷&lt;leizongmin@gmail.com&gt;</span></div><div class='line' id='LC379'><span class="cm"> */</span></div><div class='line' id='LC380'><br/></div><div class='line' id='LC381'><span class="kd">var</span> <span class="nx">DEFAULT</span> <span class="o">=</span> <span class="nx">require</span><span class="p">(</span><span class="s1">&#39;./default&#39;</span><span class="p">);</span></div><div class='line' id='LC382'><span class="kd">var</span> <span class="nx">parser</span> <span class="o">=</span> <span class="nx">require</span><span class="p">(</span><span class="s1">&#39;./parser&#39;</span><span class="p">);</span></div><div class='line' id='LC383'><span class="kd">var</span> <span class="nx">FilterXSS</span> <span class="o">=</span> <span class="nx">require</span><span class="p">(</span><span class="s1">&#39;./xss&#39;</span><span class="p">);</span></div><div class='line' id='LC384'><br/></div><div class='line' id='LC385'><br/></div><div class='line' id='LC386'><span class="cm">/**</span></div><div class='line' id='LC387'><span class="cm"> * XSS过滤</span></div><div class='line' id='LC388'><span class="cm"> *</span></div><div class='line' id='LC389'><span class="cm"> * @param {String} html 要过滤的HTML代码</span></div><div class='line' id='LC390'><span class="cm"> * @param {Object} options 选项：whiteList, onTag, onTagAttr, onIgnoreTag, onIgnoreTagAttr, safeAttrValue, escapeHtml</span></div><div class='line' id='LC391'><span class="cm"> * @return {String}</span></div><div class='line' id='LC392'><span class="cm"> */</span></div><div class='line' id='LC393'><span class="kd">function</span> <span class="nx">filterXSS</span> <span class="p">(</span><span class="nx">html</span><span class="p">,</span> <span class="nx">options</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC394'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">xss</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">FilterXSS</span><span class="p">(</span><span class="nx">options</span><span class="p">);</span></div><div class='line' id='LC395'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">xss</span><span class="p">.</span><span class="nx">process</span><span class="p">(</span><span class="nx">html</span><span class="p">);</span></div><div class='line' id='LC396'><span class="p">}</span></div><div class='line' id='LC397'><br/></div><div class='line' id='LC398'><br/></div><div class='line' id='LC399'><span class="c1">// 输出</span></div><div class='line' id='LC400'><span class="nx">exports</span> <span class="o">=</span> <span class="nx">module</span><span class="p">.</span><span class="nx">exports</span> <span class="o">=</span> <span class="nx">filterXSS</span><span class="p">;</span></div><div class='line' id='LC401'><span class="nx">exports</span><span class="p">.</span><span class="nx">FilterXSS</span> <span class="o">=</span> <span class="nx">FilterXSS</span><span class="p">;</span></div><div class='line' id='LC402'><span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="k">in</span> <span class="nx">DEFAULT</span><span class="p">)</span> <span class="nx">exports</span><span class="p">[</span><span class="nx">i</span><span class="p">]</span> <span class="o">=</span> <span class="nx">DEFAULT</span><span class="p">[</span><span class="nx">i</span><span class="p">];</span></div><div class='line' id='LC403'><span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="k">in</span> <span class="nx">parser</span><span class="p">)</span> <span class="nx">exports</span><span class="p">[</span><span class="nx">i</span><span class="p">]</span> <span class="o">=</span> <span class="nx">parser</span><span class="p">[</span><span class="nx">i</span><span class="p">];</span></div><div class='line' id='LC404'><br/></div><div class='line' id='LC405'><br/></div><div class='line' id='LC406'><span class="c1">// 在浏览器端使用</span></div><div class='line' id='LC407'><span class="k">if</span> <span class="p">(</span><span class="k">typeof</span> <span class="nb">window</span> <span class="o">!==</span> <span class="s1">&#39;undefined&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC408'>&nbsp;&nbsp;<span class="c1">// 低版本浏览器支持</span></div><div class='line' id='LC409'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nb">Array</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC410'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nb">Array</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">indexOf</span> <span class="o">=</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">item</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC411'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">for</span><span class="p">(</span><span class="kd">var</span> <span class="nx">i</span><span class="o">=</span><span class="mi">0</span><span class="p">;</span><span class="nx">i</span><span class="o">&lt;</span><span class="k">this</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span><span class="nx">i</span><span class="o">++</span><span class="p">){</span></div><div class='line' id='LC412'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span><span class="p">(</span><span class="k">this</span><span class="p">[</span><span class="nx">i</span><span class="p">]</span> <span class="o">==</span> <span class="nx">item</span><span class="p">)</span> <span class="k">return</span> <span class="nx">i</span><span class="p">;</span></div><div class='line' id='LC413'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC414'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="o">-</span><span class="mi">1</span><span class="p">;</span></div><div class='line' id='LC415'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC416'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC417'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nb">Array</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">forEach</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC418'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nb">Array</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">forEach</span> <span class="o">=</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">fn</span><span class="p">,</span> <span class="nx">scope</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC419'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span> <span class="nx">i</span> <span class="o">&lt;</span> <span class="k">this</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="nx">fn</span><span class="p">.</span><span class="nx">call</span><span class="p">(</span><span class="nx">scope</span><span class="p">,</span> <span class="k">this</span><span class="p">[</span><span class="nx">i</span><span class="p">],</span> <span class="nx">i</span><span class="p">,</span> <span class="k">this</span><span class="p">);</span></div><div class='line' id='LC420'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC421'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC422'>&nbsp;&nbsp;<span class="k">if</span><span class="p">(</span><span class="o">!</span><span class="nb">String</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">trim</span><span class="p">){</span></div><div class='line' id='LC423'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nb">String</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">trim</span> <span class="o">=</span> <span class="kd">function</span> <span class="p">()</span> <span class="p">{</span></div><div class='line' id='LC424'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="k">this</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="sr">/(^\s*)|(\s*$)/g</span><span class="p">,</span> <span class="s1">&#39;&#39;</span><span class="p">);</span></div><div class='line' id='LC425'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC426'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC427'>&nbsp;&nbsp;<span class="c1">// 输出</span></div><div class='line' id='LC428'>&nbsp;&nbsp;<span class="nb">window</span><span class="p">.</span><span class="nx">filterXSS</span> <span class="o">=</span> <span class="nx">module</span><span class="p">.</span><span class="nx">exports</span><span class="p">;</span></div><div class='line' id='LC429'><span class="p">}</span></div><div class='line' id='LC430'><br/></div><div class='line' id='LC431'><span class="p">},{</span><span class="s2">&quot;./default&quot;</span><span class="o">:</span><span class="mi">1</span><span class="p">,</span><span class="s2">&quot;./parser&quot;</span><span class="o">:</span><span class="mi">3</span><span class="p">,</span><span class="s2">&quot;./xss&quot;</span><span class="o">:</span><span class="mi">4</span><span class="p">}],</span><span class="mi">3</span><span class="o">:</span><span class="p">[</span><span class="kd">function</span><span class="p">(</span><span class="nx">require</span><span class="p">,</span><span class="nx">module</span><span class="p">,</span><span class="nx">exports</span><span class="p">){</span></div><div class='line' id='LC432'><span class="cm">/**</span></div><div class='line' id='LC433'><span class="cm"> * 简单 HTML Parser</span></div><div class='line' id='LC434'><span class="cm"> *</span></div><div class='line' id='LC435'><span class="cm"> * @author 老雷&lt;leizongmin@gmail.com&gt;</span></div><div class='line' id='LC436'><span class="cm"> */</span></div><div class='line' id='LC437'><br/></div><div class='line' id='LC438'><br/></div><div class='line' id='LC439'><span class="cm">/**</span></div><div class='line' id='LC440'><span class="cm"> * 获取标签的名称</span></div><div class='line' id='LC441'><span class="cm"> *</span></div><div class='line' id='LC442'><span class="cm"> * @param {String} html 如：&#39;&lt;a hef=&quot;#&quot;&gt;&#39;</span></div><div class='line' id='LC443'><span class="cm"> * @return {String}</span></div><div class='line' id='LC444'><span class="cm"> */</span></div><div class='line' id='LC445'><span class="kd">function</span> <span class="nx">getTagName</span> <span class="p">(</span><span class="nx">html</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC446'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="s1">&#39; &#39;</span><span class="p">);</span></div><div class='line' id='LC447'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">i</span> <span class="o">===</span> <span class="o">-</span><span class="mi">1</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC448'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">tagName</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="mi">1</span><span class="p">,</span> <span class="o">-</span><span class="mi">1</span><span class="p">);</span></div><div class='line' id='LC449'>&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC450'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">tagName</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="mi">1</span><span class="p">,</span> <span class="nx">i</span> <span class="o">+</span> <span class="mi">1</span><span class="p">);</span></div><div class='line' id='LC451'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC452'>&nbsp;&nbsp;<span class="nx">tagName</span> <span class="o">=</span> <span class="nx">tagName</span><span class="p">.</span><span class="nx">trim</span><span class="p">().</span><span class="nx">toLowerCase</span><span class="p">();</span></div><div class='line' id='LC453'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">tagName</span><span class="p">[</span><span class="mi">0</span><span class="p">]</span> <span class="o">===</span> <span class="s1">&#39;/&#39;</span><span class="p">)</span> <span class="nx">tagName</span> <span class="o">=</span> <span class="nx">tagName</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="mi">1</span><span class="p">);</span></div><div class='line' id='LC454'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">tagName</span><span class="p">[</span><span class="nx">tagName</span><span class="p">.</span><span class="nx">length</span> <span class="o">-</span> <span class="mi">1</span><span class="p">]</span> <span class="o">===</span> <span class="s1">&#39;/&#39;</span><span class="p">)</span> <span class="nx">tagName</span> <span class="o">=</span> <span class="nx">tagName</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="mi">0</span><span class="p">,</span> <span class="o">-</span><span class="mi">1</span><span class="p">);</span></div><div class='line' id='LC455'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">tagName</span><span class="p">;</span></div><div class='line' id='LC456'><span class="p">}</span></div><div class='line' id='LC457'><br/></div><div class='line' id='LC458'><span class="cm">/**</span></div><div class='line' id='LC459'><span class="cm"> * 是否为闭合标签</span></div><div class='line' id='LC460'><span class="cm"> *</span></div><div class='line' id='LC461'><span class="cm"> * @param {String} html 如：&#39;&lt;a hef=&quot;#&quot;&gt;&#39;</span></div><div class='line' id='LC462'><span class="cm"> * @return {Boolean}</span></div><div class='line' id='LC463'><span class="cm"> */</span></div><div class='line' id='LC464'><span class="kd">function</span> <span class="nx">isClosing</span> <span class="p">(</span><span class="nx">html</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC465'>&nbsp;&nbsp;<span class="k">return</span> <span class="p">(</span><span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="mi">0</span><span class="p">,</span> <span class="mi">2</span><span class="p">)</span> <span class="o">===</span> <span class="s1">&#39;&lt;/&#39;</span><span class="p">);</span></div><div class='line' id='LC466'><span class="p">}</span></div><div class='line' id='LC467'><br/></div><div class='line' id='LC468'><span class="cm">/**</span></div><div class='line' id='LC469'><span class="cm"> * 分析HTML代码，调用相应的函数处理，返回处理后的HTML</span></div><div class='line' id='LC470'><span class="cm"> *</span></div><div class='line' id='LC471'><span class="cm"> * @param {String} html</span></div><div class='line' id='LC472'><span class="cm"> * @param {Function} onTag 处理标签的函数</span></div><div class='line' id='LC473'><span class="cm"> *   参数格式： function (sourcePosition, position, tag, html, isClosing)</span></div><div class='line' id='LC474'><span class="cm"> * @param {Function} escapeHtml 对HTML进行转义的韩松</span></div><div class='line' id='LC475'><span class="cm"> * @return {String}</span></div><div class='line' id='LC476'><span class="cm"> */</span></div><div class='line' id='LC477'><span class="kd">function</span> <span class="nx">parseTag</span> <span class="p">(</span><span class="nx">html</span><span class="p">,</span> <span class="nx">onTag</span><span class="p">,</span> <span class="nx">escapeHtml</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC478'>&nbsp;&nbsp;<span class="s1">&#39;user strict&#39;</span><span class="p">;</span></div><div class='line' id='LC479'><br/></div><div class='line' id='LC480'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">rethtml</span> <span class="o">=</span> <span class="s1">&#39;&#39;</span><span class="p">;</span>        <span class="c1">// 待返回的HTML</span></div><div class='line' id='LC481'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">lastPos</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span>         <span class="c1">// 上一个标签结束位置</span></div><div class='line' id='LC482'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">tagStart</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span>    <span class="c1">// 当前标签开始位置</span></div><div class='line' id='LC483'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">quoteStart</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span>  <span class="c1">// 引号开始位置</span></div><div class='line' id='LC484'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">currentPos</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span>      <span class="c1">// 当前位置</span></div><div class='line' id='LC485'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">len</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span>   <span class="c1">// HTML长度</span></div><div class='line' id='LC486'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">currentHtml</span> <span class="o">=</span> <span class="s1">&#39;&#39;</span><span class="p">;</span>    <span class="c1">// 当前标签的HTML代码</span></div><div class='line' id='LC487'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">currentTagName</span> <span class="o">=</span> <span class="s1">&#39;&#39;</span><span class="p">;</span> <span class="c1">// 当前标签的名称</span></div><div class='line' id='LC488'><br/></div><div class='line' id='LC489'>&nbsp;&nbsp;<span class="c1">// 逐个分析字符</span></div><div class='line' id='LC490'>&nbsp;&nbsp;<span class="k">for</span> <span class="p">(</span><span class="nx">currentPos</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span> <span class="nx">currentPos</span> <span class="o">&lt;</span> <span class="nx">len</span><span class="p">;</span> <span class="nx">currentPos</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC491'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">c</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">currentPos</span><span class="p">);</span></div><div class='line' id='LC492'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">tagStart</span> <span class="o">===</span> <span class="kc">false</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC493'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">c</span> <span class="o">===</span> <span class="s1">&#39;&lt;&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC494'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">tagStart</span> <span class="o">=</span> <span class="nx">currentPos</span><span class="p">;</span></div><div class='line' id='LC495'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">continue</span><span class="p">;</span></div><div class='line' id='LC496'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC497'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC498'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">quoteStart</span> <span class="o">===</span> <span class="kc">false</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC499'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">c</span> <span class="o">===</span> <span class="s1">&#39;&lt;&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC500'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">rethtml</span> <span class="o">+=</span> <span class="nx">escapeHtml</span><span class="p">(</span><span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">lastPos</span><span class="p">,</span> <span class="nx">currentPos</span><span class="p">));</span></div><div class='line' id='LC501'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">tagStart</span> <span class="o">=</span> <span class="nx">currentPos</span><span class="p">;</span></div><div class='line' id='LC502'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">lastPos</span> <span class="o">=</span> <span class="nx">currentPos</span><span class="p">;</span></div><div class='line' id='LC503'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">continue</span><span class="p">;</span></div><div class='line' id='LC504'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC505'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">c</span> <span class="o">===</span> <span class="s1">&#39;&gt;&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC506'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">rethtml</span> <span class="o">+=</span> <span class="nx">escapeHtml</span><span class="p">(</span><span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">lastPos</span><span class="p">,</span> <span class="nx">tagStart</span><span class="p">));</span></div><div class='line' id='LC507'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">currentHtml</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">tagStart</span><span class="p">,</span> <span class="nx">currentPos</span> <span class="o">+</span> <span class="mi">1</span><span class="p">);</span></div><div class='line' id='LC508'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">currentTagName</span> <span class="o">=</span> <span class="nx">getTagName</span><span class="p">(</span><span class="nx">currentHtml</span><span class="p">);</span></div><div class='line' id='LC509'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">rethtml</span> <span class="o">+=</span> <span class="nx">onTag</span><span class="p">(</span><span class="nx">tagStart</span><span class="p">,</span></div><div class='line' id='LC510'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">rethtml</span><span class="p">.</span><span class="nx">length</span><span class="p">,</span></div><div class='line' id='LC511'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">currentTagName</span><span class="p">,</span></div><div class='line' id='LC512'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">currentHtml</span><span class="p">,</span></div><div class='line' id='LC513'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">isClosing</span><span class="p">(</span><span class="nx">currentHtml</span><span class="p">));</span></div><div class='line' id='LC514'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">lastPos</span> <span class="o">=</span> <span class="nx">currentPos</span> <span class="o">+</span> <span class="mi">1</span><span class="p">;</span></div><div class='line' id='LC515'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">tagStart</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span></div><div class='line' id='LC516'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">continue</span><span class="p">;</span></div><div class='line' id='LC517'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC518'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">c</span> <span class="o">===</span> <span class="s1">&#39;&quot;&#39;</span> <span class="o">||</span> <span class="nx">c</span> <span class="o">===</span> <span class="s2">&quot;&#39;&quot;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC519'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">quoteStart</span> <span class="o">=</span> <span class="nx">c</span><span class="p">;</span></div><div class='line' id='LC520'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">continue</span><span class="p">;</span></div><div class='line' id='LC521'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC522'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC523'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">c</span> <span class="o">===</span> <span class="nx">quoteStart</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC524'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">quoteStart</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span></div><div class='line' id='LC525'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">continue</span><span class="p">;</span></div><div class='line' id='LC526'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC527'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC528'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC529'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC530'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">lastPos</span> <span class="o">&lt;</span> <span class="nx">html</span><span class="p">.</span><span class="nx">length</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC531'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">rethtml</span> <span class="o">+=</span> <span class="nx">escapeHtml</span><span class="p">(</span><span class="nx">html</span><span class="p">.</span><span class="nx">substr</span><span class="p">(</span><span class="nx">lastPos</span><span class="p">));</span></div><div class='line' id='LC532'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC533'><br/></div><div class='line' id='LC534'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">rethtml</span><span class="p">;</span></div><div class='line' id='LC535'><span class="p">}</span></div><div class='line' id='LC536'><br/></div><div class='line' id='LC537'><span class="c1">// 不符合属性名称规则的正则表达式</span></div><div class='line' id='LC538'><span class="kd">var</span> <span class="nx">REGEXP_ATTR_NAME</span> <span class="o">=</span> <span class="sr">/[^a-zA-Z0-9_:\.\-]/img</span><span class="p">;</span></div><div class='line' id='LC539'><br/></div><div class='line' id='LC540'><span class="cm">/**</span></div><div class='line' id='LC541'><span class="cm"> * 分析标签HTML代码，调用相应的函数处理，返回HTML</span></div><div class='line' id='LC542'><span class="cm"> *</span></div><div class='line' id='LC543'><span class="cm"> * @param {String} html 如标签&#39;&lt;a href=&quot;#&quot; target=&quot;_blank&quot;&gt;&#39; 则为 &#39;href=&quot;#&quot; target=&quot;_blank&quot;&#39;</span></div><div class='line' id='LC544'><span class="cm"> * @param {Function} onAttr 处理属性值的函数</span></div><div class='line' id='LC545'><span class="cm"> *   函数格式： function (name, value)</span></div><div class='line' id='LC546'><span class="cm"> * @return {String}</span></div><div class='line' id='LC547'><span class="cm"> */</span></div><div class='line' id='LC548'><span class="kd">function</span> <span class="nx">parseAttr</span> <span class="p">(</span><span class="nx">html</span><span class="p">,</span> <span class="nx">onAttr</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC549'>&nbsp;&nbsp;<span class="s1">&#39;user strict&#39;</span><span class="p">;</span></div><div class='line' id='LC550'><br/></div><div class='line' id='LC551'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">lastPos</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span>        <span class="c1">// 当前位置</span></div><div class='line' id='LC552'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">retAttrs</span> <span class="o">=</span> <span class="p">[];</span>      <span class="c1">// 待返回的属性列表</span></div><div class='line' id='LC553'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">tmpName</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span>    <span class="c1">// 临时属性名称</span></div><div class='line' id='LC554'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">len</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span>  <span class="c1">// HTML代码长度</span></div><div class='line' id='LC555'><br/></div><div class='line' id='LC556'>&nbsp;&nbsp;<span class="kd">function</span> <span class="nx">addAttr</span> <span class="p">(</span><span class="nx">name</span><span class="p">,</span> <span class="nx">value</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC557'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">name</span> <span class="o">=</span>  <span class="nx">name</span><span class="p">.</span><span class="nx">trim</span><span class="p">();</span></div><div class='line' id='LC558'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">name</span> <span class="o">=</span> <span class="nx">name</span><span class="p">.</span><span class="nx">replace</span><span class="p">(</span><span class="nx">REGEXP_ATTR_NAME</span><span class="p">,</span> <span class="s1">&#39;&#39;</span><span class="p">).</span><span class="nx">toLowerCase</span><span class="p">();</span></div><div class='line' id='LC559'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">name</span><span class="p">.</span><span class="nx">length</span> <span class="o">&lt;</span> <span class="mi">1</span><span class="p">)</span> <span class="k">return</span><span class="p">;</span></div><div class='line' id='LC560'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">retAttrs</span><span class="p">.</span><span class="nx">push</span><span class="p">(</span><span class="nx">onAttr</span><span class="p">(</span><span class="nx">name</span><span class="p">,</span> <span class="nx">value</span> <span class="o">||</span> <span class="s1">&#39;&#39;</span><span class="p">));</span></div><div class='line' id='LC561'>&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC562'><br/></div><div class='line' id='LC563'>&nbsp;&nbsp;<span class="c1">// 逐个分析字符</span></div><div class='line' id='LC564'>&nbsp;&nbsp;<span class="k">for</span> <span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span> <span class="nx">i</span> <span class="o">&lt;</span> <span class="nx">len</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC565'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">c</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">charAt</span><span class="p">(</span><span class="nx">i</span><span class="p">),</span><span class="nx">v</span><span class="p">;</span></div><div class='line' id='LC566'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">tmpName</span> <span class="o">===</span> <span class="kc">false</span> <span class="o">&amp;&amp;</span> <span class="nx">c</span> <span class="o">===</span> <span class="s1">&#39;=&#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC567'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">tmpName</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">lastPos</span><span class="p">,</span> <span class="nx">i</span><span class="p">);</span></div><div class='line' id='LC568'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">lastPos</span> <span class="o">=</span> <span class="nx">i</span> <span class="o">+</span> <span class="mi">1</span><span class="p">;</span></div><div class='line' id='LC569'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">continue</span><span class="p">;</span></div><div class='line' id='LC570'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC571'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">tmpName</span> <span class="o">!==</span> <span class="kc">false</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC572'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">i</span> <span class="o">===</span> <span class="nx">lastPos</span> <span class="o">&amp;&amp;</span> <span class="p">(</span><span class="nx">c</span> <span class="o">===</span> <span class="s1">&#39;&quot;&#39;</span> <span class="o">||</span> <span class="nx">c</span> <span class="o">===</span> <span class="s2">&quot;&#39;&quot;</span><span class="p">))</span> <span class="p">{</span></div><div class='line' id='LC573'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">j</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="nx">c</span><span class="p">,</span> <span class="nx">i</span> <span class="o">+</span> <span class="mi">1</span><span class="p">);</span></div><div class='line' id='LC574'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">j</span> <span class="o">===</span> <span class="o">-</span><span class="mi">1</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC575'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">break</span><span class="p">;</span></div><div class='line' id='LC576'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC577'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">v</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">lastPos</span> <span class="o">+</span> <span class="mi">1</span><span class="p">,</span> <span class="nx">j</span><span class="p">).</span><span class="nx">trim</span><span class="p">();</span></div><div class='line' id='LC578'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">addAttr</span><span class="p">(</span><span class="nx">tmpName</span><span class="p">,</span> <span class="nx">v</span><span class="p">);</span></div><div class='line' id='LC579'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">tmpName</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span></div><div class='line' id='LC580'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">i</span> <span class="o">=</span> <span class="nx">j</span><span class="p">;</span></div><div class='line' id='LC581'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">lastPos</span> <span class="o">=</span> <span class="nx">i</span> <span class="o">+</span> <span class="mi">1</span><span class="p">;</span></div><div class='line' id='LC582'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">continue</span><span class="p">;</span></div><div class='line' id='LC583'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC584'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC585'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC586'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">c</span> <span class="o">===</span> <span class="s1">&#39; &#39;</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC587'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">v</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">lastPos</span><span class="p">,</span> <span class="nx">i</span><span class="p">).</span><span class="nx">trim</span><span class="p">();</span></div><div class='line' id='LC588'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">tmpName</span> <span class="o">===</span> <span class="kc">false</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC589'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">addAttr</span><span class="p">(</span><span class="nx">v</span><span class="p">);</span></div><div class='line' id='LC590'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC591'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">addAttr</span><span class="p">(</span><span class="nx">tmpName</span><span class="p">,</span> <span class="nx">v</span><span class="p">);</span></div><div class='line' id='LC592'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC593'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">tmpName</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span></div><div class='line' id='LC594'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">lastPos</span> <span class="o">=</span> <span class="nx">i</span> <span class="o">+</span> <span class="mi">1</span><span class="p">;</span></div><div class='line' id='LC595'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">continue</span><span class="p">;</span></div><div class='line' id='LC596'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC597'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC598'><br/></div><div class='line' id='LC599'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">lastPos</span> <span class="o">&lt;</span> <span class="nx">html</span><span class="p">.</span><span class="nx">length</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC600'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">tmpName</span> <span class="o">===</span> <span class="kc">false</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC601'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">addAttr</span><span class="p">(</span><span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">lastPos</span><span class="p">));</span></div><div class='line' id='LC602'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC603'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">addAttr</span><span class="p">(</span><span class="nx">tmpName</span><span class="p">,</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">lastPos</span><span class="p">));</span></div><div class='line' id='LC604'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC605'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC606'><br/></div><div class='line' id='LC607'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">retAttrs</span><span class="p">.</span><span class="nx">join</span><span class="p">(</span><span class="s1">&#39; &#39;</span><span class="p">).</span><span class="nx">trim</span><span class="p">();</span></div><div class='line' id='LC608'><span class="p">}</span></div><div class='line' id='LC609'><br/></div><div class='line' id='LC610'><span class="nx">exports</span><span class="p">.</span><span class="nx">parseTag</span> <span class="o">=</span> <span class="nx">parseTag</span><span class="p">;</span></div><div class='line' id='LC611'><span class="nx">exports</span><span class="p">.</span><span class="nx">parseAttr</span> <span class="o">=</span> <span class="nx">parseAttr</span><span class="p">;</span></div><div class='line' id='LC612'><br/></div><div class='line' id='LC613'><span class="p">},{}],</span><span class="mi">4</span><span class="o">:</span><span class="p">[</span><span class="kd">function</span><span class="p">(</span><span class="nx">require</span><span class="p">,</span><span class="nx">module</span><span class="p">,</span><span class="nx">exports</span><span class="p">){</span></div><div class='line' id='LC614'><span class="cm">/**</span></div><div class='line' id='LC615'><span class="cm"> * 过滤XSS</span></div><div class='line' id='LC616'><span class="cm"> *</span></div><div class='line' id='LC617'><span class="cm"> * @author 老雷&lt;leizongmin@gmail.com&gt;</span></div><div class='line' id='LC618'><span class="cm"> */</span></div><div class='line' id='LC619'><br/></div><div class='line' id='LC620'><span class="kd">var</span> <span class="nx">DEFAULT</span> <span class="o">=</span> <span class="nx">require</span><span class="p">(</span><span class="s1">&#39;./default&#39;</span><span class="p">);</span></div><div class='line' id='LC621'><span class="kd">var</span> <span class="nx">parser</span> <span class="o">=</span> <span class="nx">require</span><span class="p">(</span><span class="s1">&#39;./parser&#39;</span><span class="p">);</span></div><div class='line' id='LC622'><span class="kd">var</span> <span class="nx">parseTag</span> <span class="o">=</span> <span class="nx">parser</span><span class="p">.</span><span class="nx">parseTag</span><span class="p">;</span></div><div class='line' id='LC623'><span class="kd">var</span> <span class="nx">parseAttr</span> <span class="o">=</span> <span class="nx">parser</span><span class="p">.</span><span class="nx">parseAttr</span><span class="p">;</span></div><div class='line' id='LC624'><br/></div><div class='line' id='LC625'><br/></div><div class='line' id='LC626'><span class="cm">/**</span></div><div class='line' id='LC627'><span class="cm"> * 返回值是否为空</span></div><div class='line' id='LC628'><span class="cm"> *</span></div><div class='line' id='LC629'><span class="cm"> * @param {Object} obj</span></div><div class='line' id='LC630'><span class="cm"> * @return {Boolean}</span></div><div class='line' id='LC631'><span class="cm"> */</span></div><div class='line' id='LC632'><span class="kd">function</span> <span class="nx">isNull</span> <span class="p">(</span><span class="nx">obj</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC633'>&nbsp;&nbsp;<span class="k">return</span> <span class="p">(</span><span class="nx">obj</span> <span class="o">===</span> <span class="kc">undefined</span> <span class="o">||</span> <span class="nx">obj</span> <span class="o">===</span> <span class="kc">null</span><span class="p">);</span></div><div class='line' id='LC634'><span class="p">}</span></div><div class='line' id='LC635'><br/></div><div class='line' id='LC636'><span class="cm">/**</span></div><div class='line' id='LC637'><span class="cm"> * 取标签内的属性列表字符串</span></div><div class='line' id='LC638'><span class="cm"> *</span></div><div class='line' id='LC639'><span class="cm"> * @param {String} html</span></div><div class='line' id='LC640'><span class="cm"> * @return {Object}</span></div><div class='line' id='LC641'><span class="cm"> *   - {String} html</span></div><div class='line' id='LC642'><span class="cm"> *   - {Boolean} closing</span></div><div class='line' id='LC643'><span class="cm"> */</span></div><div class='line' id='LC644'><span class="kd">function</span> <span class="nx">getAttrs</span> <span class="p">(</span><span class="nx">html</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC645'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="s1">&#39; &#39;</span><span class="p">);</span></div><div class='line' id='LC646'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">i</span> <span class="o">===</span> <span class="o">-</span><span class="mi">1</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC647'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="p">{</span></div><div class='line' id='LC648'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">html</span><span class="o">:</span>    <span class="s1">&#39;&#39;</span><span class="p">,</span></div><div class='line' id='LC649'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">closing</span><span class="o">:</span> <span class="p">(</span><span class="nx">html</span><span class="p">[</span><span class="nx">html</span><span class="p">.</span><span class="nx">length</span> <span class="o">-</span> <span class="mi">2</span><span class="p">]</span> <span class="o">===</span> <span class="s1">&#39;/&#39;</span><span class="p">)</span></div><div class='line' id='LC650'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC651'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC652'>&nbsp;&nbsp;<span class="nx">html</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="nx">i</span> <span class="o">+</span> <span class="mi">1</span><span class="p">,</span> <span class="o">-</span><span class="mi">1</span><span class="p">).</span><span class="nx">trim</span><span class="p">();</span></div><div class='line' id='LC653'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">isClosing</span> <span class="o">=</span> <span class="p">(</span><span class="nx">html</span><span class="p">[</span><span class="nx">html</span><span class="p">.</span><span class="nx">length</span> <span class="o">-</span> <span class="mi">1</span><span class="p">]</span> <span class="o">===</span> <span class="s1">&#39;/&#39;</span><span class="p">);</span></div><div class='line' id='LC654'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">isClosing</span><span class="p">)</span> <span class="nx">html</span> <span class="o">=</span> <span class="nx">html</span><span class="p">.</span><span class="nx">slice</span><span class="p">(</span><span class="mi">0</span><span class="p">,</span> <span class="o">-</span><span class="mi">1</span><span class="p">).</span><span class="nx">trim</span><span class="p">();</span></div><div class='line' id='LC655'>&nbsp;&nbsp;<span class="k">return</span> <span class="p">{</span></div><div class='line' id='LC656'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">html</span><span class="o">:</span>    <span class="nx">html</span><span class="p">,</span></div><div class='line' id='LC657'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">closing</span><span class="o">:</span> <span class="nx">isClosing</span></div><div class='line' id='LC658'>&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC659'><span class="p">}</span></div><div class='line' id='LC660'><br/></div><div class='line' id='LC661'><span class="cm">/**</span></div><div class='line' id='LC662'><span class="cm"> * XSS过滤对象</span></div><div class='line' id='LC663'><span class="cm"> *</span></div><div class='line' id='LC664'><span class="cm"> * @param {Object} options 选项：whiteList, onTag, onTagAttr, onIgnoreTag,</span></div><div class='line' id='LC665'><span class="cm"> *                               onIgnoreTagAttr, safeAttrValue, escapeHtml</span></div><div class='line' id='LC666'><span class="cm"> *                               stripIgnoreTagBody, allowCommentTag</span></div><div class='line' id='LC667'><span class="cm"> */</span></div><div class='line' id='LC668'><span class="kd">function</span> <span class="nx">FilterXSS</span> <span class="p">(</span><span class="nx">options</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC669'>&nbsp;&nbsp;<span class="nx">options</span> <span class="o">=</span> <span class="nx">options</span> <span class="o">||</span> <span class="p">{};</span></div><div class='line' id='LC670'><br/></div><div class='line' id='LC671'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">options</span><span class="p">.</span><span class="nx">stripIgnoreTag</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC672'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">options</span><span class="p">.</span><span class="nx">onIgnoreTag</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC673'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">console</span><span class="p">.</span><span class="nx">error</span><span class="p">(</span><span class="s1">&#39;Notes: cannot use these two options &quot;stripIgnoreTag&quot; and &quot;onIgnoreTag&quot; at the same time&#39;</span><span class="p">);</span></div><div class='line' id='LC674'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC675'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">options</span><span class="p">.</span><span class="nx">onIgnoreTag</span> <span class="o">=</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">onIgnoreTagStripAll</span><span class="p">;</span></div><div class='line' id='LC676'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC677'><br/></div><div class='line' id='LC678'>&nbsp;&nbsp;<span class="nx">options</span><span class="p">.</span><span class="nx">whiteList</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">whiteList</span> <span class="o">||</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">whiteList</span><span class="p">;</span></div><div class='line' id='LC679'>&nbsp;&nbsp;<span class="nx">options</span><span class="p">.</span><span class="nx">onTag</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">onTag</span> <span class="o">||</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">onTag</span><span class="p">;</span></div><div class='line' id='LC680'>&nbsp;&nbsp;<span class="nx">options</span><span class="p">.</span><span class="nx">onTagAttr</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">onTagAttr</span> <span class="o">||</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">onTagAttr</span><span class="p">;</span></div><div class='line' id='LC681'>&nbsp;&nbsp;<span class="nx">options</span><span class="p">.</span><span class="nx">onIgnoreTag</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">onIgnoreTag</span> <span class="o">||</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">onIgnoreTag</span><span class="p">;</span></div><div class='line' id='LC682'>&nbsp;&nbsp;<span class="nx">options</span><span class="p">.</span><span class="nx">onIgnoreTagAttr</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">onIgnoreTagAttr</span> <span class="o">||</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">onIgnoreTagAttr</span><span class="p">;</span></div><div class='line' id='LC683'>&nbsp;&nbsp;<span class="nx">options</span><span class="p">.</span><span class="nx">safeAttrValue</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">safeAttrValue</span> <span class="o">||</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">safeAttrValue</span><span class="p">;</span></div><div class='line' id='LC684'>&nbsp;&nbsp;<span class="nx">options</span><span class="p">.</span><span class="nx">escapeHtml</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">escapeHtml</span> <span class="o">||</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">escapeHtml</span><span class="p">;</span></div><div class='line' id='LC685'>&nbsp;&nbsp;<span class="k">this</span><span class="p">.</span><span class="nx">options</span> <span class="o">=</span> <span class="nx">options</span><span class="p">;</span></div><div class='line' id='LC686'><span class="p">}</span></div><div class='line' id='LC687'><br/></div><div class='line' id='LC688'><span class="cm">/**</span></div><div class='line' id='LC689'><span class="cm"> * 开始处理</span></div><div class='line' id='LC690'><span class="cm"> *</span></div><div class='line' id='LC691'><span class="cm"> * @param {String} html</span></div><div class='line' id='LC692'><span class="cm"> * @return {String}</span></div><div class='line' id='LC693'><span class="cm"> */</span></div><div class='line' id='LC694'><span class="nx">FilterXSS</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">process</span> <span class="o">=</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">html</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC695'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">me</span> <span class="o">=</span> <span class="k">this</span><span class="p">;</span></div><div class='line' id='LC696'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">options</span> <span class="o">=</span> <span class="nx">me</span><span class="p">.</span><span class="nx">options</span><span class="p">;</span></div><div class='line' id='LC697'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">whiteList</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">whiteList</span><span class="p">;</span></div><div class='line' id='LC698'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">onTag</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">onTag</span><span class="p">;</span></div><div class='line' id='LC699'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">onIgnoreTag</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">onIgnoreTag</span><span class="p">;</span></div><div class='line' id='LC700'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">onTagAttr</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">onTagAttr</span><span class="p">;</span></div><div class='line' id='LC701'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">onIgnoreTagAttr</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">onIgnoreTagAttr</span><span class="p">;</span></div><div class='line' id='LC702'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">safeAttrValue</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">safeAttrValue</span><span class="p">;</span></div><div class='line' id='LC703'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">escapeHtml</span> <span class="o">=</span> <span class="nx">options</span><span class="p">.</span><span class="nx">escapeHtml</span></div><div class='line' id='LC704'><br/></div><div class='line' id='LC705'>&nbsp;&nbsp;<span class="c1">// 是否禁止备注标签</span></div><div class='line' id='LC706'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">options</span><span class="p">.</span><span class="nx">allowCommentTag</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC707'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">html</span> <span class="o">=</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">stripCommentTag</span><span class="p">(</span><span class="nx">html</span><span class="p">);</span></div><div class='line' id='LC708'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC709'><br/></div><div class='line' id='LC710'>&nbsp;&nbsp;<span class="c1">// 如果开启了stripIgnoreTagBody</span></div><div class='line' id='LC711'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">options</span><span class="p">.</span><span class="nx">stripIgnoreTagBody</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC712'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">stripIgnoreTagBody</span> <span class="o">=</span> <span class="nx">DEFAULT</span><span class="p">.</span><span class="nx">StripTagBody</span><span class="p">(</span><span class="nx">options</span><span class="p">.</span><span class="nx">stripIgnoreTagBody</span><span class="p">,</span> <span class="nx">onIgnoreTag</span><span class="p">);</span></div><div class='line' id='LC713'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">onIgnoreTag</span> <span class="o">=</span> <span class="nx">stripIgnoreTagBody</span><span class="p">.</span><span class="nx">onIgnoreTag</span><span class="p">;</span></div><div class='line' id='LC714'>&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC715'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">stripIgnoreTagBody</span> <span class="o">=</span> <span class="kc">false</span><span class="p">;</span></div><div class='line' id='LC716'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC717'><br/></div><div class='line' id='LC718'>&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">retHtml</span> <span class="o">=</span> <span class="nx">parseTag</span><span class="p">(</span><span class="nx">html</span><span class="p">,</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">sourcePosition</span><span class="p">,</span> <span class="nx">position</span><span class="p">,</span> <span class="nx">tag</span><span class="p">,</span> <span class="nx">html</span><span class="p">,</span> <span class="nx">isClosing</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC719'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">info</span> <span class="o">=</span> <span class="p">{</span></div><div class='line' id='LC720'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">sourcePosition</span><span class="o">:</span> <span class="nx">sourcePosition</span><span class="p">,</span></div><div class='line' id='LC721'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">position</span><span class="o">:</span>       <span class="nx">position</span><span class="p">,</span></div><div class='line' id='LC722'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">isClosing</span><span class="o">:</span>      <span class="nx">isClosing</span><span class="p">,</span></div><div class='line' id='LC723'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">isWhite</span><span class="o">:</span>        <span class="p">(</span><span class="nx">tag</span> <span class="k">in</span> <span class="nx">whiteList</span><span class="p">)</span></div><div class='line' id='LC724'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">};</span></div><div class='line' id='LC725'><br/></div><div class='line' id='LC726'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 调用onTag处理</span></div><div class='line' id='LC727'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">ret</span> <span class="o">=</span> <span class="nx">onTag</span><span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">html</span><span class="p">,</span> <span class="nx">info</span><span class="p">);</span></div><div class='line' id='LC728'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">isNull</span><span class="p">(</span><span class="nx">ret</span><span class="p">))</span> <span class="k">return</span> <span class="nx">ret</span><span class="p">;</span></div><div class='line' id='LC729'><br/></div><div class='line' id='LC730'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 默认标签处理方法</span></div><div class='line' id='LC731'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">info</span><span class="p">.</span><span class="nx">isWhite</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC732'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 白名单标签，解析标签属性</span></div><div class='line' id='LC733'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 如果是闭合标签，则不需要解析属性</span></div><div class='line' id='LC734'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">info</span><span class="p">.</span><span class="nx">isClosing</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC735'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="s1">&#39;&lt;/&#39;</span> <span class="o">+</span> <span class="nx">tag</span> <span class="o">+</span> <span class="s1">&#39;&gt;&#39;</span><span class="p">;</span></div><div class='line' id='LC736'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC737'><br/></div><div class='line' id='LC738'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">attrs</span> <span class="o">=</span> <span class="nx">getAttrs</span><span class="p">(</span><span class="nx">html</span><span class="p">);</span></div><div class='line' id='LC739'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">whiteAttrList</span> <span class="o">=</span> <span class="nx">whiteList</span><span class="p">[</span><span class="nx">tag</span><span class="p">];</span></div><div class='line' id='LC740'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">attrsHtml</span> <span class="o">=</span> <span class="nx">parseAttr</span><span class="p">(</span><span class="nx">attrs</span><span class="p">.</span><span class="nx">html</span><span class="p">,</span> <span class="kd">function</span> <span class="p">(</span><span class="nx">name</span><span class="p">,</span> <span class="nx">value</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC741'><br/></div><div class='line' id='LC742'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 调用onTagAttr处理</span></div><div class='line' id='LC743'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">isWhiteAttr</span> <span class="o">=</span> <span class="p">(</span><span class="nx">whiteAttrList</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="nx">name</span><span class="p">)</span> <span class="o">!==</span> <span class="o">-</span><span class="mi">1</span><span class="p">);</span></div><div class='line' id='LC744'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">ret</span> <span class="o">=</span> <span class="nx">onTagAttr</span><span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">name</span><span class="p">,</span> <span class="nx">value</span><span class="p">,</span> <span class="nx">isWhiteAttr</span><span class="p">);</span></div><div class='line' id='LC745'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">isNull</span><span class="p">(</span><span class="nx">ret</span><span class="p">))</span> <span class="k">return</span> <span class="nx">ret</span><span class="p">;</span></div><div class='line' id='LC746'><br/></div><div class='line' id='LC747'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 默认的属性处理方法</span></div><div class='line' id='LC748'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">isWhiteAttr</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC749'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 白名单属性，调用safeAttrValue过滤属性值</span></div><div class='line' id='LC750'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">value</span> <span class="o">=</span> <span class="nx">safeAttrValue</span><span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">name</span><span class="p">,</span> <span class="nx">value</span><span class="p">);</span></div><div class='line' id='LC751'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">value</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC752'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">name</span> <span class="o">+</span> <span class="s1">&#39;=&quot;&#39;</span> <span class="o">+</span> <span class="nx">value</span> <span class="o">+</span> <span class="s1">&#39;&quot;&#39;</span><span class="p">;</span></div><div class='line' id='LC753'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC754'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">name</span><span class="p">;</span></div><div class='line' id='LC755'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC756'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC757'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 非白名单属性，调用onIgnoreTagAttr处理</span></div><div class='line' id='LC758'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">ret</span> <span class="o">=</span> <span class="nx">onIgnoreTagAttr</span><span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">name</span><span class="p">,</span> <span class="nx">value</span><span class="p">,</span> <span class="nx">isWhiteAttr</span><span class="p">);</span></div><div class='line' id='LC759'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">isNull</span><span class="p">(</span><span class="nx">ret</span><span class="p">))</span> <span class="k">return</span> <span class="nx">ret</span><span class="p">;</span></div><div class='line' id='LC760'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span><span class="p">;</span></div><div class='line' id='LC761'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC762'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">});</span></div><div class='line' id='LC763'><br/></div><div class='line' id='LC764'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 构造新的标签代码</span></div><div class='line' id='LC765'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">html</span> <span class="o">=</span> <span class="s1">&#39;&lt;&#39;</span> <span class="o">+</span> <span class="nx">tag</span><span class="p">;</span></div><div class='line' id='LC766'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">attrsHtml</span><span class="p">)</span> <span class="nx">html</span> <span class="o">+=</span> <span class="s1">&#39; &#39;</span> <span class="o">+</span> <span class="nx">attrsHtml</span><span class="p">;</span></div><div class='line' id='LC767'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">attrs</span><span class="p">.</span><span class="nx">closing</span><span class="p">)</span> <span class="nx">html</span> <span class="o">+=</span> <span class="s1">&#39; /&#39;</span><span class="p">;</span></div><div class='line' id='LC768'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">html</span> <span class="o">+=</span> <span class="s1">&#39;&gt;&#39;</span><span class="p">;</span></div><div class='line' id='LC769'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">html</span><span class="p">;</span></div><div class='line' id='LC770'><br/></div><div class='line' id='LC771'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span> <span class="k">else</span> <span class="p">{</span></div><div class='line' id='LC772'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="c1">// 非白名单标签，调用onIgnoreTag处理</span></div><div class='line' id='LC773'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="kd">var</span> <span class="nx">ret</span> <span class="o">=</span> <span class="nx">onIgnoreTag</span><span class="p">(</span><span class="nx">tag</span><span class="p">,</span> <span class="nx">html</span><span class="p">,</span> <span class="nx">info</span><span class="p">);</span></div><div class='line' id='LC774'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nx">isNull</span><span class="p">(</span><span class="nx">ret</span><span class="p">))</span> <span class="k">return</span> <span class="nx">ret</span><span class="p">;</span></div><div class='line' id='LC775'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="k">return</span> <span class="nx">escapeHtml</span><span class="p">(</span><span class="nx">html</span><span class="p">);</span></div><div class='line' id='LC776'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC777'><br/></div><div class='line' id='LC778'>&nbsp;&nbsp;<span class="p">},</span> <span class="nx">escapeHtml</span><span class="p">);</span></div><div class='line' id='LC779'><br/></div><div class='line' id='LC780'>&nbsp;&nbsp;<span class="c1">// 如果开启了stripIgnoreTagBody，需要对结果再进行处理</span></div><div class='line' id='LC781'>&nbsp;&nbsp;<span class="k">if</span> <span class="p">(</span><span class="nx">stripIgnoreTagBody</span><span class="p">)</span> <span class="p">{</span></div><div class='line' id='LC782'>&nbsp;&nbsp;&nbsp;&nbsp;<span class="nx">retHtml</span> <span class="o">=</span> <span class="nx">stripIgnoreTagBody</span><span class="p">.</span><span class="nx">remove</span><span class="p">(</span><span class="nx">retHtml</span><span class="p">);</span></div><div class='line' id='LC783'>&nbsp;&nbsp;<span class="p">}</span></div><div class='line' id='LC784'><br/></div><div class='line' id='LC785'>&nbsp;&nbsp;<span class="k">return</span> <span class="nx">retHtml</span><span class="p">;</span></div><div class='line' id='LC786'><span class="p">};</span></div><div class='line' id='LC787'><br/></div><div class='line' id='LC788'><br/></div><div class='line' id='LC789'><span class="nx">module</span><span class="p">.</span><span class="nx">exports</span> <span class="o">=</span> <span class="nx">FilterXSS</span><span class="p">;</span></div><div class='line' id='LC790'><span class="p">},{</span><span class="s2">&quot;./default&quot;</span><span class="o">:</span><span class="mi">1</span><span class="p">,</span><span class="s2">&quot;./parser&quot;</span><span class="o">:</span><span class="mi">3</span><span class="p">}]},{},[</span><span class="mi">2</span><span class="p">])</span></div></pre></div></td>
         </tr>
       </table>
  </div>

  </div>
</div>

<a href="#jump-to-line" rel="facebox[.linejump]" data-hotkey="l" class="js-jump-to-line" style="display:none">Jump to Line</a>
<div id="jump-to-line" style="display:none">
  <form accept-charset="UTF-8" class="js-jump-to-line-form">
    <input class="linejump-input js-jump-to-line-field" type="text" placeholder="Jump to line&hellip;" autofocus>
    <button type="submit" class="button">Go</button>
  </form>
</div>

        </div>

      </div><!-- /.repo-container -->
      <div class="modal-backdrop"></div>
    </div><!-- /.container -->
  </div><!-- /.site -->


    </div><!-- /.wrapper -->

      <div class="container">
  <div class="site-footer">
    <ul class="site-footer-links right">
      <li><a href="https://status.github.com/">Status</a></li>
      <li><a href="http://developer.github.com">API</a></li>
      <li><a href="http://training.github.com">Training</a></li>
      <li><a href="http://shop.github.com">Shop</a></li>
      <li><a href="/blog">Blog</a></li>
      <li><a href="/about">About</a></li>

    </ul>

    <a href="/" aria-label="Homepage">
      <span class="mega-octicon octicon-mark-github" title="GitHub"></span>
    </a>

    <ul class="site-footer-links">
      <li>&copy; 2014 <span title="0.12764s from github-fe128-cp1-prd.iad.github.net">GitHub</span>, Inc.</li>
        <li><a href="/site/terms">Terms</a></li>
        <li><a href="/site/privacy">Privacy</a></li>
        <li><a href="/security">Security</a></li>
        <li><a href="/contact">Contact</a></li>
    </ul>
  </div><!-- /.site-footer -->
</div><!-- /.container -->


    <div class="fullscreen-overlay js-fullscreen-overlay" id="fullscreen_overlay">
  <div class="fullscreen-container js-fullscreen-container">
    <div class="textarea-wrap">
      <textarea name="fullscreen-contents" id="fullscreen-contents" class="fullscreen-contents js-fullscreen-contents" placeholder="" data-suggester="fullscreen_suggester"></textarea>
    </div>
  </div>
  <div class="fullscreen-sidebar">
    <a href="#" class="exit-fullscreen js-exit-fullscreen tooltipped tooltipped-w" aria-label="Exit Zen Mode">
      <span class="mega-octicon octicon-screen-normal"></span>
    </a>
    <a href="#" class="theme-switcher js-theme-switcher tooltipped tooltipped-w"
      aria-label="Switch themes">
      <span class="octicon octicon-color-mode"></span>
    </a>
  </div>
</div>



    <div id="ajax-error-message" class="flash flash-error">
      <span class="octicon octicon-alert"></span>
      <a href="#" class="octicon octicon-x close js-ajax-error-dismiss" aria-label="Dismiss error"></a>
      Something went wrong with that request. Please try again.
    </div>


      <script crossorigin="anonymous" src="https://assets-cdn.github.com/assets/frameworks-df9e4beac80276ed3dfa56be0d97b536d0f5ee12.js" type="text/javascript"></script>
      <script async="async" crossorigin="anonymous" src="https://assets-cdn.github.com/assets/github-22af5724b6a68093dc4ea24d753f84320ccb5dd5.js" type="text/javascript"></script>
      
      
        <script async src="https://www.google-analytics.com/analytics.js"></script>
  </body>
</html>

