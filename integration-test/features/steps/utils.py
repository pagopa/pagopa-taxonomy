def get_global_conf(context, field):
    return context.config.userdata.get("global_config").get(field)