alter table if exists voting
    add distribution_account_secret text,
    add ballot_account_secret text,
    add issuer_account_secret text,
    drop column internal_funding_account_secret;